use std::fs;
use std::collections::HashMap;
use chrono::DateTime;
use chrono::offset::Utc;
use crate::db;

fn read_folder() -> HashMap<i32, String> {

    let mut map: HashMap<i32, String> = HashMap::new();

    for entry in fs::read_dir("./src")
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

pub fn compare() {
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