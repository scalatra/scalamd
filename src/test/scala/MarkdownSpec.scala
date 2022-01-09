package org.fusesource.scalamd.test

import java.io.File
import org.fusesource.scalamd.Markdown
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.io.Source

class MarkdownSpec extends AnyWordSpec with Matchers {

  def assert(name: String) = {
    val inputFile = new File(getClass.getResource("/" + name + ".text").toURI)
    val expectationFile = new File(getClass.getResource("/" + name + ".html").toURI)
    val convertedHtml: String = Markdown(Source.fromFile(inputFile, "UTF-8").mkString).trim
    val expectedHtml: String = Source.fromFile(expectationFile, "UTF-8").mkString.trim
    convertedHtml should equal(expectedHtml)
  }

  "MarkdownProcessor" should {
    "Images" in {
      assert("Images")
    }
    "TOC" in {
      assert("TOC")
    }
    "Amps and angle encoding" in {
      assert("Amps and angle encoding")
    }
    "Auto links" in {
      assert("Auto links")
    }
    "Backslash escapes" in {
      assert("Backslash escapes")
    }
    "Blockquotes with code blocks" in {
      assert("Blockquotes with code blocks")
    }
    "Hard-wrapped paragraphs with list-like lines" in {
      assert("Hard-wrapped paragraphs with list-like lines")
    }
    "Horizontal rules" in {
      assert("Horizontal rules")
    }
    "Inline HTML (Advanced)" in {
      assert("Inline HTML (Advanced)")
    }
    "Inline HTML (Simple)" in {
      assert("Inline HTML (Simple)")
    }
    "Inline HTML comments" in {
      assert("Inline HTML comments")
    }
    "Links, inline style" in {
      assert("Links, inline style")
    }
    "Links, reference style" in {
      assert("Links, reference style")
    }
    "Literal quotes in titles" in {
      assert("Literal quotes in titles")
    }
    "Nested blockquotes" in {
      assert("Nested blockquotes")
    }
    "Ordered and unordered lists" in {
      assert("Ordered and unordered lists")
    }
    "Strong and em together" in {
      assert("Strong and em together")
    }
    "Tabs" in {
      assert("Tabs")
    }
    "Tidyness" in {
      assert("Tidyness")
    }
    "SmartyPants" in {
      assert("SmartyPants")
    }
    "Markdown inside inline HTML" in {
      assert("Markdown inside inline HTML")
    }
    "Spans inside headers" in {
      assert("Spans inside headers")
    }
    "Macros" in {
      assert("Macros")
    }
  }

}
