EXISTING_BLUE=$(docker-compose ps -q blue)

if [ -n "$EXISTING_BLUE" ]; then
   CURRENT_PROFILE="blue"
   IDLE_PROFILE="green"
   CURRENT_PORT=8080
   IDLE_PORT=8081
else
  CURRENT_PROFILE="green"
  IDLE_PROFILE="blue"
  CURRENT_PORT=8081
  IDLE_PORT=8080
fi

echo "### ${IDLE_PROFILE} (port: ${IDLE_PORT}) 배포 시작 ###"

echo "\n### 새 Docker 이미지(${DOCKER_IMAGE_NAME}) pull 시작 ###"
docker-compose pull ${IDLE_PROFILE}
echo "### 새 Docker 이미지 pull 완료 ###"

echo "\n### ${IDLE_PROFILE} 컨테이너 실행 시작 ###"
docker-compose up -d ${IDLE_PROFILE}
echo "### ${IDLE_PROFILE} 컨테이너 실행 완료 ###"

echo "### Health check 시작 ###"
echo "### curl -s http://localhost:${IDLE_PORT}/actuator/health"

for retry_count in {1..10}
do
  response=$(curl -s http://localhost:${IDLE_PORT}/actuator/health)
  up_count=$(echo ${response} | grep 'UP' | wc -l)

  if [ ${up_count} -ge 1 ]
  then
    echo "### Health check 성공 ###"
    echo "\n### Nginx 리버스 프록시 -> ${IDLE_PORT}로 변경 ###"
    echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc

    echo "\n### Nginx 리로드 ###"
    sudo nginx -s reload

    echo "\n### 이전 버전(${CURRENT_PROFILE}) 컨테이너 중지 ###"
    docker-compose stop ${CURRENT_PROFILE}
    exit 0
  else
    echo "### Health check의 응답을 알 수 없거나, 실행 상태가 아닙니다. ###"
    echo "### Health check: ${response} ###"
  fi

  if [ ${retry_count} -eq 10 ]
  then
    echo "### Health check 실패 ###"
    echo "### 배포를 종료합니다. ###"
    exit 1
  fi

  echo "### Health check 연결 실패. 5초 후 재시도... ###"
  sleep 5
done
