pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
    }

    parameters {
        string(
            name: 'APP_REPO',
            defaultValue: 'https://github.com/quintian/StockAnalyser-Web.git',
            description: 'Git repository Jenkins should clone for the StockAnalyser Flask app.'
        )
        string(
            name: 'APP_BRANCH',
            defaultValue: 'main',
            description: 'Branch to clone from the app repository.'
        )
        booleanParam(
            name: 'SMOKE_TEST_IMAGE',
            defaultValue: true,
            description: 'Run the built Docker image and check /health.'
        )
        booleanParam(
            name: 'DEPLOY',
            defaultValue: false,
            description: 'Deploy the built image to the local Ansible target.'
        )
    }

    environment {
        APP_CHECKOUT = 'stockanalyser-web'
        IMAGE_NAME = 'stockanalyser-web'
        IMAGE_TAG = "${BUILD_NUMBER}"
        TEST_CONTAINER = "stockanalyser-smoke-${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout App') {
            steps {
                dir(env.APP_CHECKOUT) {
                    git branch: params.APP_BRANCH,
                        url: params.APP_REPO
                }
            }
        }

        stage('Verify App Shape') {
            steps {
                sh '''
                    test -f "${APP_CHECKOUT}/app.py"
                    test -f "${APP_CHECKOUT}/StockAnalyser.py"
                    test -f "${APP_CHECKOUT}/requirements.txt"
                    test -f "${APP_CHECKOUT}/Dockerfile"
                    test -d "${APP_CHECKOUT}/tests"
                    git -C "${APP_CHECKOUT}" remote get-url origin
                    git -C "${APP_CHECKOUT}" rev-parse --short HEAD
                '''
            }
        }

        stage('Install Dependencies') {
            steps {
                dir(env.APP_CHECKOUT) {
                    sh '''
                        rm -rf .venv
                        python3 -m venv --copies .venv
                        . .venv/bin/activate
                        python -m pip install --upgrade pip
                        python -m pip install -r requirements.txt
                    '''
                }
            }
        }

        stage('Run Tests') {
            steps {
                dir(env.APP_CHECKOUT) {
                    sh '''
                        . .venv/bin/activate
                        coverage run -m pytest -q
                        coverage xml
                    '''
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                dir(env.APP_CHECKOUT) {
                    sh '''
                        docker build -t "${IMAGE_NAME}:${IMAGE_TAG}" -t "${IMAGE_NAME}:latest" .
                    '''
                }
            }
        }

        stage('Smoke Test Image') {
            when {
                expression { return params.SMOKE_TEST_IMAGE }
            }
            steps {
                sh '''
                    docker rm -f "${TEST_CONTAINER}" >/dev/null 2>&1 || true
                    docker run -d \
                        --name "${TEST_CONTAINER}" \
                        --network cicd-stockanalyser \
                        "${IMAGE_NAME}:${IMAGE_TAG}"

                    for attempt in $(seq 1 30); do
                        if curl -fsS "http://${TEST_CONTAINER}:8000/health"; then
                            exit 0
                        fi
                        sleep 2
                    done

                    docker logs "${TEST_CONTAINER}"
                    exit 1
                '''
            }
            post {
                always {
                    sh 'docker rm -f "${TEST_CONTAINER}" >/dev/null 2>&1 || true'
                }
            }
        }

        stage('Deploy With Ansible') {
            when {
                expression { return params.DEPLOY }
            }
            steps {
                sh '''
                    ansible-playbook \
                        -i /workspace/cicd-stockanalyser/ansible/inventory.ini \
                        /workspace/cicd-stockanalyser/ansible/deploy-stockanalyser.yml \
                        --extra-vars "stockanalyser_image=${IMAGE_NAME}:${IMAGE_TAG}"
                '''
            }
        }
    }
}
