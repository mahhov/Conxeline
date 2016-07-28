# Conxeline
~2012-2013

Youtube: https://www.youtube.com/playlist?list=PLameShrvoeYc5PnsJiSr0RhDrEKXPSGYB  
The bottom 2 videos are of the graphics engine  
The top 5 videos are after enviornment intereaction was implemented.

Project Idea  
Uses a blend of Voxel and Mesh polygons (divides the map into voxels which contain polygon data). Thus, z-ordering can be quickly done by the cpu thanks to voxels, while you still get smooth not-cubic polygons unlike pure voxel engines.

Features:  
skip rendering hidden surfaces  
dynamic lighting (supports adding/moving/removing light sources)  
terraforming / interacting with the world  
not voxel-limited (can handle non-cubic polygons, non grid (integere) coordinates, object rotations)  
completely cpu-renderable, without requiring preprocessing
