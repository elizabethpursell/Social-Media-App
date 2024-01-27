# Web Front-End
## Running the Web Frontend Locally
1. Enable CORS and start up the app with `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'config:set team-snems CORS_ENABLED=true'`
2. Go to the web folder using `cd web`
3. Deploy the frontend using `sh local-deploy.sh`
4. Follow the link provided to the webpage
5. Remember to disable CORS with `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'config:set team-snems CORS_ENABLED=false'`
6. Remember to distable the app with `ssh -i ~/.ssh/id_ed25519 -t dokku@dokku.cse.lehigh.edu 'ps:stop team-snems'`

TS docs can be found at `/docs`