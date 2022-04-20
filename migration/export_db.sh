#!/bin/bash
PGPASSWORD="kadhf8984slkf" pg_dump bbr -h bbr-db.cxyfkap4uohq.eu-west-1.rds.amazonaws.com -U bbr --exclude-table-data django_session > bbr_export.sql