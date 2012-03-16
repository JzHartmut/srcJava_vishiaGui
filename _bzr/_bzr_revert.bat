if exist .bzr goto :exec
  REM copy it to parent dir and execute.
  copy _bzr_revert.bat ..
  cd ..
  _bzr_revert.bat
  goto :ende
:exec
rmdir /S/Q _bzr
rmdir /S/Q _make
rmdir /S/Q _cmpn
rmdir /S/Q org
rmdir /S/Q cfg
rmdir /S/Q img
rmdir /S/Q rpy
if exist __Resync rmdir /S/Q __Resync
mkdir __Resync
call bzr export __Resync --per-file-timestamps
move __Resync\_bzr .\_bzr
move __Resync\_make .\_make
move __Resync\_cmpn .\_cmpn
move __Resync\org .\org
move __Resync\cfg .\cfg
move __Resync\img .\img
move __Resync\rpy .\rpy
::pause
::bzr revert
echo bzr_version
bzr version-info >_bzr_version.txt
:ende 