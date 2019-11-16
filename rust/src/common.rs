use chrono::prelude::*;

pub struct Post {
    pub id: i32,
    pub title: String,
    pub date: String,
    pub url: String,
    pub cat: String,
    pub author: String,
    pub mtime: String,
    pub content: String,
}

impl Post {
    pub fn new() -> Post {
        let utc: DateTime<Utc> = Utc::now();
        let dt = utc.format("%Y-%m-%d").to_string();
        let time = utc.format("%Y%m%d%H%M%S").to_string();
        let url = String::from(dt.clone() + ".html");
        
        Post {
            id: -1,
            date: dt,
            mtime: time,
            title: String::from("No title"),
            author: String::from("Cheng"),
            url: url,
            cat: String::new(),
            content: String::new(),
        }
    }
}