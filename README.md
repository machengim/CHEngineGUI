CHEngine
---

This tiny project aims to generate a simple website from markdown files. Although there have been many great applications like hugo and hexo, they seem too complicated for users with little knowledge about programming. It comes with a simple GUI and three built-in themes, and has the ability to adapt other templates easily.

This program is compiled under jdk12.0.1 and javafx12.0.1.

![](./chengine_demo.gif)

---

The markdown features which can be used in this app include:
+ `#` represents `<h1>`, `##` represent `<h2>`, and so on;
+ `>` represents `<blockquote>`;
+ `+` represents `<li>`;
+ more than three spaces at the start of a line represent `<pre><code>`;
+ `***` or `---` represents `<hr>`;
+ `[text](url)` represents `<a href=url>text</a>`;
+ `![text](url)` represents `<img src=url>`;
+ `*` represents `<em>`, `**` `<strong>`, \` `<code>`;
+ a single newline character at the end of a line represents `<br>`, while more than one, `<p>`.

---

A python version without GUI has been added.