from diagrams import Cluster, Diagram, Edge
from diagrams.aws.compute import EC2
from diagrams.aws.database import RDS
from diagrams.aws.integration import SQS
from diagrams.aws.network import ELB
from diagrams.aws.storage import S3
from diagrams.onprem.ci import Jenkins
from diagrams.onprem.client import Client, User, Users
from diagrams.onprem.compute import Server
from diagrams.onprem.container import Docker
from diagrams.onprem.database import PostgreSQL
from diagrams.onprem.monitoring import Grafana, Prometheus
from diagrams.onprem.network import Internet
from diagrams.programming.framework import Spring, React

graph_attr = {
  "fontsize": "20",
  "bgcolor": "white" #transparent
}

with Diagram("Application Architecture", graph_attr=graph_attr, outformat="png", filename="application_architecture"):
  ELB("lb") >> EC2("web") >> RDS("userdb") >> S3("store")
  ELB("lb") >> EC2("web") >> RDS("userdb") << EC2("stat")
  (ELB("lb") >> EC2("web")) - EC2("web") >> RDS("userdb")

  with Cluster("Application Context"):
    app = EC2("Spring Boot")

  ELB("lb") >> app

  metrics = Prometheus("metric")
  metrics << Edge(color="firebrick", style="dashed") << Grafana("monitoring")

  Jenkins("CI")
  client = Client("A")
  client >> User("B") >> Users("S")
  client >> PostgreSQL("Database")
  client >> Internet("Remote API")
  client >> Docker("Docker")
  client >> Server("Server")
  client >> SQS("Sync Books")
  client >> Spring("Backend")
  client >> React("React")
