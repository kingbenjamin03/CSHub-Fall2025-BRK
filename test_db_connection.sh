#!/bin/bash
mysql -h 127.0.0.1 -P 3306 -u root -ppw5330 -e "USE cshub; SELECT 'Database connection successful!' AS Status;"
