use std::fs;
use serde_json::Value;
use std::collections::HashMap;
use postgres::{Client, NoTls};
use crate::common;

pub fn delete(id: i32) {
    let mut conn = get_conn();

    conn.execute("delete from post where id = $1", &[&id])
                .expect("Delete error");
}

pub fn execute(post: &common::Post, op: &str) {
    match op {
        "insert" => insert(post),
        "update" => update(post),
        _ => (),
    }
}

pub fn get_conn() -> Client {
    let filename = format!("{}/{}", common::DIR, "config.json");
    let data = fs::read_to_string(filename)
                    .expect("Read file error");
    let v: Value = serde_json::from_str(&data)
                .expect("Can't parse json data");
    
    let mut map: HashMap<String, String> = HashMap::new();
    let metas = ["name", "username", "password", "address", "port"];
    for meta in &metas {
        let value = v["db"][meta].as_str().expect("");
        map.insert(meta.to_string(), value.to_string());
    }
    
    let db_str = format!("postgres://{}:{}@{}:{}/{}", map["username"],
            map["password"], map["address"], map["port"], map["name"]);
    let conn = Client::connect(&db_str, NoTls)
        .expect("Db connection error");

    conn
}

pub fn get_post_list() -> HashMap<i32, String> {
    let mut conn = get_conn();
    
    let sql = "SELECT id, mtime FROM post";
    let mut map: HashMap<i32, String> = HashMap::new();

    for row in conn.query(sql, &[]).expect("query error") {
        let id: i32 = row.get(0);
        let mtime: String = row.get(1);
        map.insert(id, mtime);
    }

    map
}

fn insert(post: &common::Post) {
    let mut conn = get_conn();

    conn.execute("insert into post(id, title, mtime, url, cat, author, date, content)
                values($1, $2, $3, $4, $5, $6, $7, $8)",
                &[&post.id, &post.title, &post.mtime, &post.url, &post.cat, &post.author, &post.date, &post.content])
                .expect("insert error");
}

fn update(post: &common::Post) {
    let mut conn = get_conn();
    let id = post.id;

    conn.execute("update post set (title, mtime, url, cat, author, date, content)
                 = ($1, $2, $3, $4, $5, $6,$7 ) where id = $8",
                 &[&post.title, &post.mtime, &post.url, &post.cat, &post.author, &post.date, &post.content, &id])
                 .expect("update error");

}
