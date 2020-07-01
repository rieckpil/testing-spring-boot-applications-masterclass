from diagrams import Cluster, Diagram
from diagrams.aws.integration import SQS
from diagrams.onprem.client import Users
from diagrams.onprem.compute import Server
from diagrams.onprem.database import PostgreSQL
from diagrams.onprem.network import Internet
from diagrams.programming.framework import Spring, React

graph_attr = {
  "fontsize": "20",
  "bgcolor": "white"  # transparent
}

with Diagram("", direction="LR", graph_attr=graph_attr, outformat="png", filename="book-reviewr-application-architecture"):
  with Cluster("Book Reviewr"):
    frontend = React("Frontend")
    backend = Spring("Backend")

  queue = SQS("SQS (Messaging Queue)")
  users = Users("Users")
  database = PostgreSQL("PostgreSQL (Database)")
  keycloak = Server("Keycloak (Identity Provider)")
  api = Internet("Open Library (REST API)")

  keycloak << [frontend, backend]

  users >> frontend
  frontend >> backend >> database
  backend >> api
  backend << queue
