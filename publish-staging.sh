#!/bin/bash

set -e
echo "Publishing to Development server"
# 138.68.182.77 is winton-staging
TARGET='138.68.182.77:/var/www/staging.childrensheartsurgery.info'
LOCAL_DEV='resources/public/'
echo "===============Building files============="
lein clean
lein home     # creates static home page for fast load
lein cljsbuild once min
mkdir -p resources/public/css
sass sass/app.scss resources/public/css/app.css
gulp
echo "===============rsync-ing files==================="
rsync -av --del --no-perms --no-owner --no-group $LOCAL_DEV $USER@$TARGET
#rsync -av $LOCAL_DEV $USER@$TARGET
echo "================================="
echo "Uploaded to " $TARGET
echo "================================="
