use std::fs;
mod parser;
mod db;
mod common;
mod manager;

fn main() {
    let filename = "src/1.md";
    let text = fs::read_to_string(filename).expect("Can't read file!");
    let post = parser::parse_md(&text);

    manager::compare();
}
