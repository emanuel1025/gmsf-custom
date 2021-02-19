**************************************************************
*        Generic Mobility Simulation Framework (GMSF)        *
**************************************************************

The Generic Mobility Simulation Framework (GMSF) was developed
by Philipp Sommer as a part of his master thesis [1] at 
ETH Zurich and has been presented at MobilityModels'08, the
first ACM SIGMOBILE workshop on Mobility models [2].

------------------------------------------------------------------------
 Mobility Models
------------------------------------------------------------------------

The simulation framework contains our new GIS-based mobility model,
the MMTS model and the common Random Waypoint and Manhattan models.

 * GIS-based mobility model: Steady-state random trips on real road
   topology from the Swiss geographic information system (GIS) [3].
   The model implements a basic car-following mechanism using the 
   Intelligent-Driver Model (IDM) [4]. Additionally, major road 
   intersections are controlled by a simple traffic light model.
   Mobility traces can be generated based on the road topology of
   three different areas in Switzerland (City, Urban and Rural scenario)
   
 * MMTS Model: Mobility model which is based on realistic vehicular
   traces and on the road topology from the Multi-agent Microscopic
   Traffic Simulator (MMTS) [5]. We provide vehicular traces for three
   different areas in Switzerland (City, Urban and Rural scenario).
   
 * Random Waypoint Model: Steady-state random trip model. The steady-state
   initialization is performed using the method described by Camp and Navidi. [6]
   
 * Manhattan Model: Nodes travel on a grid-like road network. If the
   distance to the front vehicle is below a threshold value, the speed
   is set at maximum to the speed of the front vehicle. Otherwise, nodes
   are accelerating or decelerating on a random basis while moving at a
   speed in the specified range.
   
 More details can be found in the thesis [1] or in the research paper [2]. 
 
------------------------------------------------------------------------
 Output Format
------------------------------------------------------------------------
 
Mobility traces can be generated in various output formats. GMSF supports
the mobility trace format of the popular ns-2 (incl. nam traces) and Qualnet
network simulators. In addition, we offer to generate traces in a simulator
independent XML-based trace format. 


-----------------------------------------------------------------------
 Usage
------------------------------------------------------------------------
1. Build GMSF:

$ ant build 

2. Run GMSF:

$ java -jar gmsf.jar <PARAMETERS>

where <PARAMETERS> is a set of comma-separated KEY=VALUE pairs:

INPUT_DIRECTORY=<directory containing input files>
OUTPUT_DIRECTORY=<output directory for trace files>
SIMULATION_SIZE=<size of the simulation area>
TIME=<simulation time in seconds>
SEED=<random seed value>
MODEL=<type of mobility model, valid values are RWP (Random Waypoint), MN (Manhattan), GIS (GIS-based), MMTS (MMTS traces), FIXED (no mobility)>
FORMAT=<output format for the mobility traces, valid values are QUALNET, NAM, NS-2, XML, PDF>
GUI=<1=enables/0=disables the graphical user interface>

------------------------------------------------------------------------
Examples:
------------------------------------------------------------------------

- Random Waypoint
$ java -jar gmsf.jar MODEL=RWP,SIMULATION_SIZE=1000,NODES=100,TIME=1000,FORMAT=NAM

- Manhattan
$ java -jar gmsf.jar MODEL=MN,SIMULATION_SIZE=1000,BLOCKS=10,NODES=100,TIME=1000,FORMAT=NAM
where BLOCKS=<number of blocks in one dimension>

- MMTS mobility
$ java -jar gmsf.jar MODEL=MMTS,SIMULATION_SIZE=3000,NODES=117,TIME=1000,INPUT_DIRECTORY=Rural/,FORMAT=NAM
where INPUT_DIRECTORY=<dir> specifies the directory where the corresponding MMTS traces file (mmts.dat) is located

- GIS based mobility model
$ java -jar gmsf.jar MODEL=GIS,CAR_FOLLOWING=1,TRAFFIC_LIGHTS=1,SIMULATION_SIZE=3000,NODES=100,TIME=2000,INPUT_DIRECTORY=Rural/,FORMAT=NAM
where INPUT_DIRECTORY=<dir> specifies the directory where the corresponding road topology file (roads.dat) is located
The CAR_FOLLOWING parameter specifies whether cars should respect a minimal distance to the car ahead. Cars do stop at larger intersections when the TRAFFIC_LIGHTS parameter is set to 1 (see the report for details).
 

------------------------------------------------------------------------
 References
------------------------------------------------------------------------
[1] "Design and Analysis of Realistic Mobility Model for Wireless Mesh Networks", Philipp Sommer, Master Thesis, ETH Zurich, Sept 07.
[2] "Generic mobility simulation framework (GMSF)", Rainer Baumann, Franck Legendre and Philipp Sommer, MobilityModels '08: Proceeding of the 1st ACM SIGMOBILE workshop on Mobility models, Hongkong, China.
[3] "VECTOR 25 - Landscape model of Switzerland", The Swiss Federal Office of Topography swisstopo
[4] "Congested Traffic States in Empirical Observations and Microscopic Simulations", M. Treiber , A. Hennecke and D. Helbing, Physical Review
[5] Realistic Vehicular Traces, Laboratory for Software Technology, ETH Zurich
[6] "Stationary Distributions for the Random Waypoint Mobility Model", W. Navidi and T. Camp, IEEE Transactions on Mobile Computing, Vol. 3, 2004
[7] "The IMPORTANT framework for analyzing the Impact of Mobility on Performance Of RouTing protocols for Adhoc NeTworks", F. Bai, N. Sadagopan and A. Helmy, INFOCOM 2003
