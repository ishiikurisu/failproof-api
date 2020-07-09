Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.network "private_network", ip: "77.77.7.77"

  config.vm.provision "shell", inline: <<-SHELL
    apt-get update
    apt-get install -y nginx postgresql postgresql-client git
  SHELL
end
