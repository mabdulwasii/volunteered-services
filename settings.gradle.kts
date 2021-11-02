rootProject.name = "volunteered-server"
include("apps:auth")
findProject(":apps:auth")?.name = "auth"
