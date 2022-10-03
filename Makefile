cluster/start:
	docker-compose exec rabbit2 rabbitmqctl stop_app
	docker-compose exec rabbit2 rabbitmqctl reset
	docker-compose exec rabbit2 rabbitmqctl join_cluster rabbit@rabbit1
	docker-compose exec rabbit2 rabbitmqctl start_app
	docker-compose exec rabbit2 rabbitmqctl cluster_status
	docker-compose exec rabbit3 rabbitmqctl stop_app
	docker-compose exec rabbit3 rabbitmqctl reset
	docker-compose exec rabbit3 rabbitmqctl join_cluster rabbit@rabbit1
	docker-compose exec rabbit3 rabbitmqctl start_app
	docker-compose exec rabbit3 rabbitmqctl cluster_status