#!/bin/sh
chown postgres:postgres /var/lib/postgresql/config/*
cp /var/lib/postgresql/config/pg_hba.conf /var/lib/postgresql/data/pgdata/pg_hba.conf.old
