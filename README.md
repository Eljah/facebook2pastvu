# facebook2pastvu

This web application scans posts of a Facebook group and tries to find matching photos on [PastVu](https://pastvu.com). When a match is found it comments a link to the PastVu photo under the original Facebook post and stores the result in a SQLite database.

## Building

```
mvn package
```

This produces `facebook2pastvu.war` which can be deployed to Tomcat.

## Configuration

Open `/config` (HTTP Basic auth, password `admin` by default) to set the required keys:

- `FB_TOKEN` – Facebook API token with permissions to manage the group
- `FB_GROUP_ID` – ID of the Facebook group
- `PASTVU_CITY` – PastVu city ID used for image search
- `SCHEDULE_MINUTES` – interval in minutes between sync runs (default `60`)

The application downloads images to the `downloads/` directory and stores matching results in `db.sqlite` (up to 10 000 entries).

## Running

Deploy the generated WAR to Tomcat. The synchronization job is started by a cron scheduler once the application context is initialized.

## Viewing results

Open `/matches` in the deployed application to see all stored matches. The page
lists the Facebook post ID, thumbnails from both sources and provides direct
links to the Facebook and PastVu pages along with the matching method stored in
the database.
