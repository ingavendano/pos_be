#!/bin/bash
# Script de instalación automática para Oracle Cloud (Ubuntu ARM)

echo "--- Iniciando actualización del sistema ---"
sudo apt-get update && sudo apt-get upgrade -y

echo "--- Instalando dependencias básicas ---"
sudo apt-get install -y ca-certificates curl gnupg git

echo "--- Configurando repositorio de Docker ---"
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update

echo "--- Instalando Docker y Docker Compose ---"
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

echo "--- Configurando permisos de Docker ---"
sudo usermod -aG docker $USER

echo "--- Instalación completada con éxito ---"
echo "IMPORTANTE: Cierre la sesión (escriba 'exit') y vuelva a entrar para que los cambios tengan efecto."
