wtw-station
===========

Whatever The Weather (WTW) - Weather Station data service sub system components.

##What is it
A multi-module maven project that provides a Weather Station data service within the WTW SOA application system. 

##Component modules
1. wtw-station-service: Weather Station data service API. 
2. wtw-station-client: JAXRS service proxy for client side access to Weather Station data service API. 
3. wtw-station-rs: JAXRS Weather Station data service access.
4. wtw-station-manager: Weather Station data service API manager implementation.
5. wtw-station-source: Weather Station data source API. 
6. wtw-station-source-google: Google implementation of Weather Station data source API. 
7. wtw-station-source-met: MET Office implementation of Weather Station data source API. 
8. wtw-station-source-yahoo: Yahoo implementation of Weather Station data source API. 
8. wtw-station-source-xively: Xively, (custom weather station,) implementation of Weather Station data source API. 

##Architectural principles
1. Modularity.
2. Encapsulation, separation of concerns, Loose coupling.
3. Flexible, Extensible.
4. Distributed processing
5. Asynchronous processing
6. Variety of front ends, web, mobile, desktop
7. SOA, Web Services
8. Cloud deployment.
9. Continuous Integration, build, test, (unit, integration, UAT, performance, smoke test,) deploy.
10. Source code management with GIT, branches and master development.
11. Architect for OSGi.

## What does it look like?
wtw will be deployed on linode with UI currently prototyping on www.jimomulloy.co.uk:4000
