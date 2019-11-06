# Customize Themes Instructions - MarkdownEditTool

## Overview

You can customize your markdown preview with CSS stylesheet in MarkdownEditTool. This document instructs you how to make your own theme.

## Installation

Themes should be put in the themes folder generated automatically on its first run.

Folder location: `[jar file or Main class location]/resources/themes/`

## Syntax

You can simply use basic css syntax to write your theme.

Examples can be find in the themes folder, three basic examples are provided.

However, MarkdwonEditTool takes some settings in your stylesheet to make it look better. Here's the list:

- `body.background-color` will be taken as edit-pane's background color
- `body.color` will be taken as edit-pane's text color

## Example

this example comes from the default theme (light(default).css)

```CSS
body {
    width: 90%;
    max-width: 60em;
    margin: 10vh auto;
    background-color: #fcfeff;
    color: #222222;
    line-height: 135%;
    font-size: 1em;
    letter-spacing: 0.04em;
}

img {
    width: 60%;
    padding-left: 20%;
    margin: 2em 0;
}

pre {
    background-color: #e7e9eb;
    padding: 1em;
    border-radius: 0.4em;
}

code {
    color: rgb(148, 59, 0);
    font-family: monospace;
}

blockquote {
    border: rgb(72, 132, 245) 0.5em;
    border-left-style: solid;
    margin: 1.2em 0;
    padding: 1.6em;
    background-color: rgb(237, 237, 243);
    font-family: sans-serif;
    color: rgb(51, 83, 104);
}

em {
    padding-right: 0.2em;
}

li {
   font-family: sans-serif;
}

a {
    font-family: sans-serif;
    text-decoration: none;
    color: rgb(85, 50, 211);
    margin: 0.2em 0;
}

b {
    margin: 0.2em 0;
}

h1 {
    line-height: 150%;
    padding-bottom: 0.3em;
    text-align: center;
    font-family: sans-serif;
    font-size: 3em;
    border: rgb(125, 144, 165) 0.08em;
    border-bottom-style: solid;
    font-weight: normal;
}

h2 {
    line-height: 140%;
    padding-bottom: 0.2em;
    font-size: 2.4em;
    font-family: sans-serif;
    border: rgb(125, 144, 165) 0.02em;
    border-bottom-style: solid;
    font-weight: normal;
}

h3 {
    line-height: 130%;
    font-size: 2.1em;
    font-family: sans-serif;
    font-weight: normal;
}

h4 {
    line-height: 120%;
    font-size: 1.8em;
    margin-block-start: 1em;
    margin-block-end: 1em;
}

h5 {
    font-size: 1.6em;
    margin-block-start: 1em;
    margin-block-end: 1em;
}

h6 {
    font-size: 1.5em;
    margin-block-start: 1em;
    margin-block-end: 1em;
}
```