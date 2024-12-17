# Migrate data from PostgreSQL to MySQL/MariaDB

## Create initial MySQL schema

* Connect craft to MySQl by editing the `.env` file. Change the name of the database if it's not `craftcms`.
* Then execute `./craft insall` to create all required tables.
* Empty all tables, so you get the schema only.

```shell
echo "SET FOREIGN_KEY_CHECKS = 0;" > truncate.sql
cat<<"EOF"|sudo mysql -N >> truncate.sql
SELECT CONCAT('TRUNCATE TABLE `', table_name, '`;') 
FROM information_schema.tables 
WHERE table_schema = 'craftcms';
EOF
echo "SET FOREIGN_KEY_CHECKS = 1;" >> truncate.sql
sudo mysql craftcms < truncate.sql
```

## Export and prepare the PostgreSQL data

Follow [these](https://servd.host/blog/converting-craft-cms-from-postgres-to-mysql) instructions.
After having exported the inserts and before importing to MySQL, remove statements MySQL doesn't understand from the
inserts.sql.

```sql
# Remove the 'public' schema assertions
sed -i 's/"public"\.//g' inserts.sql

# Replace double quotes around column names with backticks
perl -pi.bak -e 's/"(?=.*\) VALUES \()/`/g' inserts.sql

# Escape any backslashes - a weird thing that MySQL needs
perl -pi.bak -e 's/\\/\\\\/g' inserts.sql

# Remove all SET and SELECT statements. MySQL will not understand them.
sed -i 's/^SET [a-z _]* = .*\;$//g' inserts.sql
sed -i 's/^SELECT pg_catalog.set_config.*\;$//g' inserts.sql

# Disable foreigne key checks during the import
sed -i '1s/^/SET foreign_key_checks = 0;\n/' inserts.sql
echo "SET foreign_key_checks = 1;" >> inserts.sql
```

## Create additional tables and columns in MySQL
The content table will very likely have additional columns and tables created by your project.
Dump the table schema from your PostgreSQL database and inspect these columns.

```shell
pg_dump -U postgres -d dev_craftcms -s -h localhost -p 5432 > schema_psql.psql
```

Create an ALTER TABLE ADD COLUMN statements for all columns prefixed with `field_`.
Keep in mind, MySQL supports maximum VARCHAR(255). What's beyond this limit must be created with column type `TEXT`.

What's `timestamp(0) without time zone` becomes `TIMESTAMP` in MySQL.

For an export created 2024-03-21 you can apply `prepare-mysql-for-import.sql` to your MySQL database to create all
required tables and columns.

## Import the data into MySQL

On a large inserts.sql file, it's recommended to use `pv` to supervise the progress.
On a small file you can remove pv from the pipe.

```shell
# Using pv to show the progress
pv -s $(stat --format=%s inserts.sql)< inserts.sql | sudo mysql -f craftcms

# Without pv
sudo mysql -f craftcms < inserts.sql
```

## Remove or change the table prefix

```shell
TABLES=$(mysql craftcms -e "show tables" -N)
OLD_PREFIX="defau_"
NEW_PREFIX=""
echo "SET foreign_key_checks = 0;" > rename_tables.sql 
for TABLE in $TABLES;do
  NEW_TABLE=$(sed s/$OLD_PREFIX/$NEW_PREFIX/ <<<$TABLE)
  echo "RENAME TABLE $TABLE TO $NEW_TABLE;" >> rename_tables.sql 
done
echo "SET foreign_key_checks = 1;" >> rename_tables.sql
mysql craftcms < rename_tables.sql
```