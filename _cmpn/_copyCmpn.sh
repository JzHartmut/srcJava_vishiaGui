## This shell script copies the version info to this. It gathers the last commited version,
## because the bzr start batch calls bzr version-info after any invocation.
## The special java program checks and gets the components then.

cp ../../srcJava_vishiaBase/_bzr_version.txt srcJava_vishiaBase.cmpn 
cp ../../srcJava_vishiaRun/_bzr_version.txt  srcJava_vishiaRun.cmpn  
cp ../../srcJava_Zbnf/_bzr_version.txt       srcJava_Zbnf.cmpn       

echo copy _bzr_version done.