CHEngine
---

This tiny project aims to generate a simple website from markdown files. Although there have been many great applications like hugo and hexo, they seem too complicated for one who has little knowledge about front-end development. It comes with a simple GUI and three built-in themes, and has the ability to adapt other templates easily.

This program is compiled under jdk12.0.1 and javafx12.0.1.

Demo: https://www.bilibili.com/video/av59689149

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