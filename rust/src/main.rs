use std::fs;
mod parser;
mod db;
mod common;
mod manager;

fn main() {
    let filename = format!("{}/{}", common::DIR, "1.md");
    let text = fs::read_to_string(filename).expect("Can't read file!");
    let post = parser::parse_md(&text);

    db::get_conn();
    manager::manage_site();
}
