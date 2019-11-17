use std::fs;
use std::path::Path;
use std::collections::HashMap;
use chrono::DateTime;
use chrono::offset::Utc;
use crate::{db, common, parser};

pub fn manage_site() {
    let (insert, delete, update) = compare();
    println!("insert: {:?}", &insert);
    println!("delete: {:?}", &delete);
    println!("update: {:?}", &update);
    insert_post(&insert);
    delete_post(&delete);
    update_post(&update);
}

fn compare() -> (Vec<i32>, Vec<i32>, Vec<i32>) {
    let md_metas = read_folder();
    let db_metas = db::get_post_list();
    let mut md_keys: Vec<i32> = md_metas.keys().cloned().collect();
    let mut db_keys: Vec<i32> = db_metas.keys().cloned().collect();
    let (mut l1, mut l2, mut l3) = (Vec::new(), Vec::new(), Vec::new());
    let (mut i, mut j) = (0, 0);

    md_keys.sort();
    db_keys.sort();
    while i < md_keys.len() && j < db_keys.len() {
        if md_keys[i] < db_keys[j] {
            l1.push(md_keys[i]);
            i += 1;
        } else if md_keys[i] > db_keys[j] {
            l2.push(db_keys[j]);
            j += 1;
        } else {
            let mtime1 = md_metas[&md_keys[i]].clone();
            let mtime2 = db_metas[&db_keys[j]].clone();
            if mtime1 > mtime2 {
                l3.push(md_keys[i]);
            }
            i += 1;
            j += 1;
        }
    }

    while i < md_keys.len() {
        l1.push(md_keys[i]);
        i += 1;
    }

    while j < db_keys.len() {
        l2.push(db_keys[j]);
        j += 1;
    }

    (l1, l2, l3)
}

fn delete_post(v: & Vec<i32>) {
    for id in v {
        db::delete(*id);
    }
}

fn get_post_from_md(id: &i32) -> common::Post {
    let filename = format!("{}/{}.md", common::DIR, id.to_string());
    if !Path::new(&filename).is_file() {
        panic!("File not found!"); 
    }

    let text = fs::read_to_string(&filename).expect("Read md file error!");
    let post = parser::parse_md(&text);
    if &post.id != id {
        panic!("Id inconsistency!");
    }

    post
}

fn get_md_mtime(file: &str) -> String {
    let metadata = fs::metadata(file)
                    .expect("Can't read metadata of file");

    let mut mtime = String::new();

    if let Ok(time) = metadata.modified() {
        let dt: DateTime<Utc> = time.into();
        mtime = dt.format("%Y%m%d%H%M%S").to_string();
    }

    mtime
}

fn get_md_id(file: &str) -> i32 {
    let mut v: Vec<&str> = file.split("/").collect();
    let filename = v.iter().rev().take(1).next()
                    .expect("Can't get filename");
    v = filename.split(".md").collect();
    let id: i32 = v[0].parse()
                    .expect("Parse filename to id error");
    
    id
}

fn insert_post(v: & Vec<i32>) {
    for id in v {
        let post = get_post_from_md(id);
        db::execute(&post, "insert");
    }
}

fn read_folder() -> HashMap<i32, String> {

    let mut map: HashMap<i32, String> = HashMap::new();

    for entry in fs::read_dir(common::DIR)
                    .expect("Directory reading error!") {
        let path = entry.unwrap().path();
        let path = path.to_str()
                    .expect("Can't convert path to str");

        if path.ends_with(".md") {
            let mtime = get_md_mtime(path);
            let id = get_md_id(path);
            map.insert(id, mtime);
        }
    }

    map
}

fn update_post(v: & Vec<i32>) {
    for id in v {
        let mut post = get_post_from_md(id);
        let utc: DateTime<Utc> = Utc::now();
        let time = utc.format("%Y%m%d%H%M%S").to_string();
        post.mtime = time;
        db::execute(&post, "update");
    }
}
