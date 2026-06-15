Copyright © 2026 Quinn Tian. All rights reserved. Personal and educational use is permitted with proper attribution. Commercial use, redistribution, or modification requires a paid commercial license.

This project creates a Jenkins CI pipeline for the StockAnalyser Flask app.

Application source:
- Jenkins clones the Flask app from GitHub.
- Default repo:
  `https://github.com/quintian/StockAnalyser-Web.git`
- The app repo should contain:
  - `app.py`
  - `StockAnalyser.py`
  - `TB3MS.csv`
  - `requirements.txt`
  - `Dockerfile`
  - `tests/`

Pipeline:
1. Jenkins starts from docker-compose.
2. Jenkins checks out the StockAnalyser Flask app.
3. Jenkins verifies the app files exist.
4. Jenkins creates a Python virtual environment.
5. Jenkins installs `requirements.txt`.
6. Jenkins runs pytest with coverage.
7. Jenkins builds the app Docker image.
8. If `SMOKE_TEST_IMAGE=true`, Jenkins runs the image and checks `/health`.
9. If `DEPLOY=true`, Jenkins runs Ansible to deploy the built image.

Ports:
- Jenkins: http://localhost:8086
- Temporary smoke-test app port: http://localhost:8092
- Deployed StockAnalyser app: http://localhost:8087

Run locally:
```bash
docker compose up --build
```

Then open Jenkins at http://localhost:8086 and run the `stockanalyser-ci` job.

Notes:
- Push `StockAnalyser-web` to GitHub before running this pipeline.
- If your GitHub repo name uses different capitalization, update the `APP_REPO` build parameter.
- Leave `DEPLOY=false` for CI only.
- Set `DEPLOY=true` to deploy the built Docker image to the local target.

Local or SVN source:
- Local source can work by mounting the app folder into Jenkins and replacing the checkout stage with a copy from that mounted path.
- SVN can work by installing Jenkins' Subversion plugin and replacing the Git checkout step with an SVN checkout.
- GitHub/GitLab is the normal business case because Jenkins can clone a clean source copy every run.

# How to Run with Git repository checkout: 

$ cd /Users/quinn/Documents/Workspace-Codex/CICD-StockAnalyser
$ docker compose up --build

Open Jenkins: 
http://localhost:8086

click 'Build with Parameters' on the left side and check: 
APP_REPO=https://github.com/quintian/StockAnalyser-Web.git
APP_BRANCH=main
SMOKE_TEST_IMAGE=true

- last step run check 'deploy' too after ansible is configured.
- open in browser and check: 

Jenkins smoke test:
Temporary internal container on port 8000

Ansible-deployed app:
http://localhost:8087
### Below is for web brower of the old App without UI
http://localhost:8087/health
http://localhost:8087/analyze?ticker=NVDA
http://localhost:8087/company-info?ticker=NVDA&index=NASDAQ

- input stock ticker, choose Market dropdown, check 'include ocmpany information', click 'Analyze stock'

## check error logs on Jenkins webpage
click on the red sqaure and see the error message on the top
Or move mouse to the build link at the bottom and show the drop down menu and selelct console output
