#!/bin/sh

users=$(awk -F: '($3>=1000)&&($1!="nobody"){print $1}' /etc/passwd)
for user in $users ; do
    runuser -l "$user" -c 'mate-wp update'
done

exit 0