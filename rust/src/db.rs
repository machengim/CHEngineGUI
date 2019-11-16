use std::collections::HashMap;
use postgres::{Client, NoTls};
use crate::common::Post;

pub fn execute(post: &Post, op: &str) {
    match op {
        "insert" => insert(post),
        _ => (),
    }
}

pub fn get_post_list() -> HashMap<i32, String> {
    let mut conn = Client::connect("postgres://postgres:xxxxxx@localhost:5432/mydb", NoTls)
                        .expect("conn error!");
    
    let sql = "SELECT id, mtime FROM post";
    let mut map: HashMap<i32, String> = HashMap::new();

    for row in conn.query(sql, &[]).expect("query error") {
        let id: i32 = row.get(0);
        let mtime: String = row.get(1);
        map.insert(id, mtime);
    }

    map
}

fn insert(post: &Post) {
    let mut conn = Client::connect("postgres://postgres:xxxxxx@localhost:5432/mydb", NoTls)
                        .expect("conn error!");

    conn.execute("insert into post(id, title, mtime, url, cat, author, date, content)
                values($1, $2, $3, $4, $5, $6, $7, $8)",
                &[&post.id, &post.title, &post.mtime, &post.url, &post.cat, &post.author, &post.date, &post.content])
                .expect("insert error");
}
