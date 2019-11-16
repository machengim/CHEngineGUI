use comrak::{markdown_to_html, ComrakOptions};
use crate::common::Post;

pub fn parse_md(text: &str) -> Post {
    let texts: Vec<&str> = text.split("---").collect();
    if texts.len() != 3 {
        panic!("MD file format error!");
    }

    let mut post = Post::new();
    parse_head(texts[1], &mut post);
    parse_body(texts[2], &mut post);
    post
}

fn parse_head(head: &str, post: &mut Post) {
    let metas = ["id", "title", "date", "url", "cat", "author"];

    for meta in &metas {
        let pat = meta.to_string() + ":";
        if let Some(start) = head.find(&pat) {
            let start = find_after(head, start, ":").expect("Error in find_after");
            if let Some(end) = find_after(head, start, "\n") {
                let value = head[start+1..end].to_string().trim().to_string();
                if value.len() > 0 {
                    match meta {
                        &"id" => {
                            let id:u32 = value.parse().expect("ID format error");
                            post.id = id as i32;
                        },
                        &"title" => post.title = value,
                        &"date" => post.date = value,
                        &"url" => post.url = value,
                        &"cat" => post.cat = value,
                        &"author" => post.author = value,
                        _ => (),
                    } 
                }
            }
        }
    }
}

fn parse_body(body: &str, post: &mut Post) {
    let content = markdown_to_html(body, &ComrakOptions::default());
    post.content = content.trim().to_string();
}

fn find_after(s: &str, at: usize, pat: &str) -> Option<usize> {
    s[at..].find(pat).map(|i| at + i)
}
