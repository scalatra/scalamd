package org.fusesource.scalamd

import java.security.SecureRandom
import java.util.Random
import java.util.regex._

import scala.language.existentials

/**
 * This utility converts a plain text written in [Markdown][1] into HTML fragment.
 * The typical usage is:
 *
 *     val md = Markdown(myMarkdownText)
 *
 *  [1]: http://daringfireball.net/projects/markdown/syntax "Markdown Syntax"
 */
object Markdown {

  // ## SmartyPants chars

  val leftQuote = "&ldquo;"
  val rightQuote = "&rdquo;"
  val dash = "&mdash;"
  val copy = "&copy;"
  val reg = "&reg;"
  val trademark = "&trade;"
  val ellipsis = "&hellip;"
  val leftArrow = "&larr;"
  val rightArrow = "&rarr;"

  // ## Commons

  val keySize = 20

  val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

  val rnd: Random = new SecureRandom()

  val blockTags: List[String] = {
    "p" :: "div" :: "h1" :: "h2" :: "h3" :: "h4" :: "h5" :: "h6" ::
      "blockquote" :: "pre" :: "table" :: "dl" :: "ol" :: "ul" :: "script" ::
      "noscript" :: "form" :: "fieldset" :: "iframe" :: "math" :: "ins" :: "del" ::
      "article" :: "aside" :: "footer" :: "header" :: "hgroup" :: "nav" :: "section" ::
      "figure" :: "video" :: "audio" :: "embed" :: "canvas" :: "address" :: "details" ::
      "object" :: Nil
  }
  val htmlNameTokenExpr = "[a-z_:][a-z0-9\\-_:.]*"

  // ## Regex patterns

  // We use precompile several regular expressions that are used for typical
  // transformations.

  // Outdent
  val rOutdent = Pattern.compile("^ {1,4}", Pattern.MULTILINE)
  // Standardize line endings
  val rLineEnds = Pattern.compile("\\r\\n|\\r")
  // Strip out whitespaces in blank lines
  val rBlankLines = Pattern.compile("^ +$", Pattern.MULTILINE)
  // Tabs
  val rTabs = Pattern.compile("\\t")
  // Trailing whitespace
  val rTrailingWS = Pattern.compile("\\s+$")
  // Start of inline HTML block
  val rInlineHtmlStart = Pattern.compile(
    "^<(" + blockTags.mkString("|") + ")\\b[^/>]*?>",
    Pattern.MULTILINE | Pattern.CASE_INSENSITIVE)
  // HTML comments
  val rHtmlComment = Pattern.compile(
    "^ {0,3}(<!--.*?-->)\\s*?(?=\\n+|\\Z)",
    Pattern.MULTILINE | Pattern.DOTALL)
  // Link definitions
  val rLinkDefinition = Pattern.compile("^ {0,3}\\[(.+)\\]:" +
    " *\\n? *<?(\\S+)>? *\\n? *" +
    "(?:[\"('](.+?)[\")'])?" +
    "(?=\\n+|\\Z)", Pattern.MULTILINE)
  // Character escaping
  val rEscAmp = Pattern.compile("&(?!#?[xX]?(?:[0-9a-fA-F]+|\\w+);)")
  val rEscLt = Pattern.compile("<(?![a-z/?\\$!])")
  val rInsideTags = Pattern.compile("<(/?" + htmlNameTokenExpr + "(?:\\s+(?:" +
    "(?:" + htmlNameTokenExpr + "\\s*=\\s*\"[^\"]*\")|" +
    "(?:" + htmlNameTokenExpr + "\\s*=\\s*'[^']*')|" +
    "(?:" + htmlNameTokenExpr + "\\s*=\\s*[a-z0-9_:.\\-]+)" +
    ")\\s*)*)/?>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE)
  // Headers
  val rH1 = Pattern.compile("^ {0,3}(\\S.*?)( *\\{#(.*?)\\})?\\n=+(?=\\n+|\\Z)", Pattern.MULTILINE)
  val rH2 = Pattern.compile("^ {0,3}(\\S.*?)( *\\{#(.*?)\\})?\\n-+(?=\\n+|\\Z)", Pattern.MULTILINE)
  val rHeaders = Pattern.compile("^(#{1,6}) *(\\S.*?)(?: *#*)?( *\\{#(.*?)\\})?$", Pattern.MULTILINE)
  // Horizontal rulers
  val rHr = Pattern.compile("^ {0,3}(?:" +
    "(?:(?:\\* *){3,})|" +
    "(?:(?:- *){3,})|" +
    "(?:(?:_ *){3,})" +
    ") *$", Pattern.MULTILINE)
  val rHtmlHr = Pattern.compile(
    "^ {0,3}(<hr.*?>)\\s*?$",
    Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE)
  // Lists
  val listExpr = "( {0,3}([-+*]|\\d+\\.) +(?s:.+?)" +
    "(?:\\Z|\\n{2,}(?![-+*]|\\s|\\d+\\.)))"
  val rSubList = Pattern.compile("^" + listExpr, Pattern.MULTILINE)
  val rList = Pattern.compile("(?<=\\n\\n|\\A\\n?)" + listExpr, Pattern.MULTILINE)
  val rListItem = Pattern.compile("(\\n)?^( *)(?:[-+*]|\\d+\\.) +" +
    "((?s:.+?)\\n{1,2})(?=\\n*(?:\\Z|\\2(?:[-+*]|\\d+\\.) +))", Pattern.MULTILINE)
  // Code blocks
  val rCodeBlock = Pattern.compile("(?<=\\n\\n|\\A\\n?)" +
    "(^ {4}(?s:.+?))(?=\\Z|\\n+ {0,3}\\S)", Pattern.MULTILINE)
  val rCodeLangId = Pattern.compile("^\\s*lang:(.+?)(?:\\n|\\Z)")
  // Block quotes
  val rBlockQuote = Pattern.compile(
    "((?:^ *>(?:.+(?:\\n|\\Z))+\\n*)+)",
    Pattern.MULTILINE)
  val rBlockQuoteTrims = Pattern.compile(
    "(?:^ *> ?)|(?:^ *$)|(?-m:\\n+$)",
    Pattern.MULTILINE)
  // Paragraphs splitter
  val rParaSplit = Pattern.compile("\\n{2,}")
  // Code spans
  val rCodeSpan = Pattern.compile("(?<!\\\\)(`+)(.+?)(?<!`)\\1(?!`)")
  val rCode = Pattern.compile("<code( .*?)?>(.*?)</code>", Pattern.DOTALL)
  // Images
  val rImage = Pattern.compile("!\\[(.*?)\\]\\((.*?)( \"(.*?)\")?\\)")
  // Backslash escapes
  val backslashEscapes = ("\\\\\\\\" -> "&#92;") ::
    ("\\\\`" -> "&#96;") ::
    ("\\\\_" -> "&#95;") ::
    ("\\\\>" -> "&gt;") ::
    ("\\\\\\*" -> "&#42;") ::
    ("\\\\\\{" -> "&#123;") ::
    ("\\\\\\}" -> "&#125;") ::
    ("\\\\\\[" -> "&#91;") ::
    ("\\\\\\]" -> "&#93;") ::
    ("\\\\\\(" -> "&#40;") ::
    ("\\\\\\)" -> "&#41;") ::
    ("\\\\#" -> "&#35;") ::
    ("\\\\\\+" -> "&#43;") ::
    ("\\\\-" -> "&#45;") ::
    ("\\\\\\." -> "&#46;") ::
    ("\\\\!" -> "&#33;") :: Nil
  // Reference-style links
  val rRefLinks = Pattern.compile("(\\[(.*?)\\] ?(?:\\n *)?\\[(.*?)\\])")
  // Inline links
  val rInlineLinks = Pattern.compile("\\[(.*?)\\]\\( *<?(.*?)>? *" +
    "((['\"])(.*?)\\4)?\\)", Pattern.DOTALL)
  // Autolinks
  val rAutoLinks = Pattern.compile("<((https?|ftp):[^'\">\\s]+)>")
  // Autoemails
  val rAutoEmail = Pattern.compile("<([-.\\w]+\\@[-a-z0-9]+(\\.[-a-z0-9]+)*\\.[a-z]+)>")
  // Ems and strongs
  val rStrong = Pattern.compile("(\\*\\*|__)(?=\\S)(.+?[*_]*)(?<=\\S)\\1")
  val rEm = Pattern.compile("(\\*|_)(?=\\S)(.+?)(?<=\\S)\\1")
  // Manual linebreaks
  val rBrs = Pattern.compile(" {2,}\n")
  // Ampersand wrapping
  val rAmp = Pattern.compile("&amp;(?!#?[xX]?(?:[0-9a-fA-F]+|\\w+);)")
  // SmartyPants
  val smartyPants = (Pattern.compile("(?<=\\s|\\A)(?:\"|&quot;)(?=\\S)") -> leftQuote) ::
    (Pattern.compile("(?<=[\\w)?!.])(?:\"|&quot;)(?=[.,;?!*)]|\\s|\\Z)") -> rightQuote) ::
    (Pattern.compile("--") -> dash) ::
    (Pattern.compile("\\(r\\)", Pattern.CASE_INSENSITIVE) -> reg) ::
    (Pattern.compile("\\(c\\)", Pattern.CASE_INSENSITIVE) -> copy) ::
    (Pattern.compile("\\(tm\\)", Pattern.CASE_INSENSITIVE) -> trademark) ::
    (Pattern.compile("\\.{3}") -> ellipsis) ::
    (Pattern.compile("&lt;-|<-") -> leftArrow) ::
    (Pattern.compile("-&gt;|->") -> rightArrow) :: Nil
  // Markdown inside inline HTML
  val rInlineMd = Pattern.compile("<!--#md-->(.*)<!--~+-->", Pattern.DOTALL)
  // Macro definitions
  val rMacroDefs = Pattern.compile("<!--#md *\"{3}(.*?)\"{3}(\\?[idmsux]+)? +\"{3}(.*?)\"{3} *-->")
  // TOC Macro
  val rToc = Pattern.compile("""\{\:toc(:(\d+)-(\d+))?\}""")

  /**
   * Convert the `source` from Markdown to HTML.
   */
  def apply(source: String): String = new MarkdownText(source).toHtml()

  var macros: List[MacroDefinition] = Nil

}
