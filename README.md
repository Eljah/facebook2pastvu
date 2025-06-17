# facebook2pastvu

This web application scans posts of a Facebook group and tries to find matching photos on [PastVu](https://pastvu.com). When a match is found it comments a link to the PastVu photo under the original Facebook post and stores the result in a SQLite database.

## Building

```
mvn package
```

This produces `facebook2pastvu.war` which can be deployed to Tomcat.

## Configuration

Set the following environment variables in the servlet container:

- `FB_TOKEN` – Facebook API token with permissions to manage the group
- `FB_GROUP_ID` – ID of the Facebook group
- `PASTVU_CITY` – PastVu city ID used for image search
- `SCHEDULE_MINUTES` – interval in minutes between sync runs (default `60`)

The application downloads images to the `downloads/` directory and stores matching results in `db.sqlite` (up to 10 000 entries).

## Running

Deploy the generated WAR to Tomcat. The synchronization job is started by a cron scheduler once the application context is initialized.
