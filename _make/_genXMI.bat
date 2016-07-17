
@echo off
if "%TMP_ZBNFJAX%" == "" set TMP_ZBNFJAX=..\..\..\tmpDocu
call setZBNFJAX_HOME.bat silent

  if exist %TMP_ZBNFJAX%/.xml goto :2
  call zbnfjax zbnf2xml -i:../ -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/.xml "-a:@filename=""" --report:%TMP_ZBNFJAX%/.zbnf.rpt
  :2
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCanvasStorage.java.xml goto :4
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralCanvasStorage.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCanvasStorage.java.xml "-a:@filename="GralCanvasStorage"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :4
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralColor.java.xml goto :6
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralColor.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralColor.java.xml "-a:@filename="GralColor"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :6
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralFileDialog_ifc.java.xml goto :8
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralFileDialog_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralFileDialog_ifc.java.xml "-a:@filename="GralFileDialog_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :8
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTextFieldUser_ifc.java.xml goto :10
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralTextFieldUser_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTextFieldUser_ifc.java.xml "-a:@filename="GralTextFieldUser_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :10
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWindow_getifc.java.xml goto :12
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralWindow_getifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWindow_getifc.java.xml "-a:@filename="GralWindow_getifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :12
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralFactory_ifc.java.xml goto :14
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralFactory_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralFactory_ifc.java.xml "-a:@filename="GralFactory_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :14
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralImageBase.java.xml goto :16
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralImageBase.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralImageBase.java.xml "-a:@filename="GralImageBase"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :16
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralMngBuild_ifc.java.xml goto :18
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralMngBuild_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralMngBuild_ifc.java.xml "-a:@filename="GralMngBuild_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :18
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralMng_ifc.java.xml goto :20
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralMng_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralMng_ifc.java.xml "-a:@filename="GralMng_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :20
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralRectangle.java.xml goto :22
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralRectangle.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralRectangle.java.xml "-a:@filename="GralRectangle"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :22
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralSetValue_ifc.java.xml goto :24
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralSetValue_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralSetValue_ifc.java.xml "-a:@filename="GralSetValue_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :24
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPlugUser_ifc.java.xml goto :26
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralPlugUser_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPlugUser_ifc.java.xml "-a:@filename="GralPlugUser_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :26
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/package-info.java.xml goto :28
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/package-info.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/package-info.java.xml "-a:@filename="package-info"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :28
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralVisibleWidgets_ifc.java.xml goto :30
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralVisibleWidgets_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralVisibleWidgets_ifc.java.xml "-a:@filename="GralVisibleWidgets_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :30
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPrimaryWindow_ifc.java.xml goto :32
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralPrimaryWindow_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPrimaryWindow_ifc.java.xml "-a:@filename="GralPrimaryWindow_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :32
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTextField_ifc.java.xml goto :34
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralTextField_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTextField_ifc.java.xml "-a:@filename="GralTextField_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :34
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWindowMng_ifc.java.xml goto :36
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralWindowMng_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWindowMng_ifc.java.xml "-a:@filename="GralWindowMng_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :36
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCanvas_ifc.java.xml goto :38
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralCanvas_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCanvas_ifc.java.xml "-a:@filename="GralCanvas_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :38
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralFont.java.xml goto :40
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralFont.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralFont.java.xml "-a:@filename="GralFont"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :40
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralMngApplAdapter_ifc.java.xml goto :42
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralMngApplAdapter_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralMngApplAdapter_ifc.java.xml "-a:@filename="GralMngApplAdapter_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :42
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCurveViewTrack_ifc.java.xml goto :44
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralCurveViewTrack_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCurveViewTrack_ifc.java.xml "-a:@filename="GralCurveViewTrack_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :44
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCurveView_ifc.java.xml goto :46
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralCurveView_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCurveView_ifc.java.xml "-a:@filename="GralCurveView_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :46
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPoint.java.xml goto :48
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralPoint.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPoint.java.xml "-a:@filename="GralPoint"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :48
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWindow_ifc.java.xml goto :50
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralWindow_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWindow_ifc.java.xml "-a:@filename="GralWindow_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :50
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWidgetCfg_ifc.java.xml goto :52
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralWidgetCfg_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWidgetCfg_ifc.java.xml "-a:@filename="GralWidgetCfg_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :52
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPlugUser2Gral_ifc.java.xml goto :54
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralPlugUser2Gral_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPlugUser2Gral_ifc.java.xml "-a:@filename="GralPlugUser2Gral_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :54
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTable_ifc.java.xml goto :56
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralTable_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTable_ifc.java.xml "-a:@filename="GralTable_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :56
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTableLine_ifc.java.xml goto :58
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralTableLine_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTableLine_ifc.java.xml "-a:@filename="GralTableLine_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :58
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTextBox_ifc.java.xml goto :60
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralTextBox_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTextBox_ifc.java.xml "-a:@filename="GralTextBox_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :60
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralUserAction.java.xml goto :62
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralUserAction.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralUserAction.java.xml "-a:@filename="GralUserAction"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :62
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWidget_ifc.java.xml goto :64
  call zbnfjax zbnf2xml -i:../org/vishia/gral/ifc/GralWidget_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWidget_ifc.java.xml "-a:@filename="GralWidget_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/ifc/.zbnf.rpt
  :64
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/area9/GuiCfg.java.xml goto :66
  call zbnfjax zbnf2xml -i:../org/vishia/gral/area9/GuiCfg.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/area9/GuiCfg.java.xml "-a:@filename="GuiCfg"" --report:%TMP_ZBNFJAX%/org/vishia/gral/area9/.zbnf.rpt
  :66
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/area9/GuiCallingArgs.java.xml goto :68
  call zbnfjax zbnf2xml -i:../org/vishia/gral/area9/GuiCallingArgs.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/area9/GuiCallingArgs.java.xml "-a:@filename="GuiCallingArgs"" --report:%TMP_ZBNFJAX%/org/vishia/gral/area9/.zbnf.rpt
  :68
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/area9/GralArea9MainCmd.java.xml goto :70
  call zbnfjax zbnf2xml -i:../org/vishia/gral/area9/GralArea9MainCmd.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/area9/GralArea9MainCmd.java.xml "-a:@filename="GralArea9MainCmd"" --report:%TMP_ZBNFJAX%/org/vishia/gral/area9/.zbnf.rpt
  :70
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/area9/package-info.java.xml goto :72
  call zbnfjax zbnf2xml -i:../org/vishia/gral/area9/package-info.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/area9/package-info.java.xml "-a:@filename="package-info"" --report:%TMP_ZBNFJAX%/org/vishia/gral/area9/.zbnf.rpt
  :72
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/area9/GralArea9Window.java.xml goto :74
  call zbnfjax zbnf2xml -i:../org/vishia/gral/area9/GralArea9Window.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/area9/GralArea9Window.java.xml "-a:@filename="GralArea9Window"" --report:%TMP_ZBNFJAX%/org/vishia/gral/area9/.zbnf.rpt
  :74
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/area9/GralArea9_ifc.java.xml goto :76
  call zbnfjax zbnf2xml -i:../org/vishia/gral/area9/GralArea9_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/area9/GralArea9_ifc.java.xml "-a:@filename="GralArea9_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/area9/.zbnf.rpt
  :76
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GetGralWidget_ifc.java.xml goto :78
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GetGralWidget_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GetGralWidget_ifc.java.xml "-a:@filename="GetGralWidget_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :78
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralMenu.java.xml goto :80
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralMenu.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralMenu.java.xml "-a:@filename="GralMenu"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :80
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralWindow_setifc.java.xml goto :82
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralWindow_setifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralWindow_setifc.java.xml "-a:@filename="GralWindow_setifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :82
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralDispatchCallbackWorker.java.xml goto :84
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralDispatchCallbackWorker.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralDispatchCallbackWorker.java.xml "-a:@filename="GralDispatchCallbackWorker"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :84
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/package-info.java.xml goto :86
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/package-info.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/package-info.java.xml "-a:@filename="package-info"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :86
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralGraphicThread.java.xml goto :88
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralGraphicThread.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralGraphicThread.java.xml "-a:@filename="GralGraphicThread"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :88
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralWindow.java.xml goto :90
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralWindow.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralWindow.java.xml "-a:@filename="GralWindow"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :90
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralTextBox.java.xml goto :92
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralTextBox.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralTextBox.java.xml "-a:@filename="GralTextBox"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :92
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralTextField.java.xml goto :94
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralTextField.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralTextField.java.xml "-a:@filename="GralTextField"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :94
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralButton.java.xml goto :96
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralButton.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralButton.java.xml "-a:@filename="GralButton"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :96
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralMouseWidgetAction_ifc.java.xml goto :98
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralMouseWidgetAction_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralMouseWidgetAction_ifc.java.xml "-a:@filename="GralMouseWidgetAction_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :98
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralLed.java.xml goto :100
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralLed.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralLed.java.xml "-a:@filename="GralLed"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :100
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralKeyListener.java.xml goto :102
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralKeyListener.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralKeyListener.java.xml "-a:@filename="GralKeyListener"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :102
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralHtmlBox.java.xml goto :104
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralHtmlBox.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralHtmlBox.java.xml "-a:@filename="GralHtmlBox"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :104
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralPos.java.xml goto :106
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralPos.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralPos.java.xml "-a:@filename="GralPos"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :106
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetChangeRequ.java.xml goto :108
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralWidgetChangeRequ.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetChangeRequ.java.xml "-a:@filename="GralWidgetChangeRequ"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :108
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralCurveView.java.xml goto :110
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralCurveView.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralCurveView.java.xml "-a:@filename="GralCurveView"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :110
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralPanelActivated_ifc.java.xml goto :112
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralPanelActivated_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralPanelActivated_ifc.java.xml "-a:@filename="GralPanelActivated_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :112
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralPanelContent.java.xml goto :114
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralPanelContent.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralPanelContent.java.xml "-a:@filename="GralPanelContent"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :114
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralGridProperties.java.xml goto :116
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralGridProperties.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralGridProperties.java.xml "-a:@filename="GralGridProperties"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :116
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetGthreadSet_ifc.java.xml goto :118
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralWidgetGthreadSet_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetGthreadSet_ifc.java.xml "-a:@filename="GralWidgetGthreadSet_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :118
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralTabbedPanel.java.xml goto :120
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralTabbedPanel.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralTabbedPanel.java.xml "-a:@filename="GralTabbedPanel"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :120
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralValueBar.java.xml goto :122
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralValueBar.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralValueBar.java.xml "-a:@filename="GralValueBar"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :122
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralWidget.java.xml goto :124
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralWidget.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralWidget.java.xml "-a:@filename="GralWidget"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :124
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralTable.java.xml goto :126
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralTable.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralTable.java.xml "-a:@filename="GralTable"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :126
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetMng.java.xml goto :128
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralWidgetMng.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetMng.java.xml "-a:@filename="GralWidgetMng"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :128
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralKeySpecial_ifc.java.xml goto :130
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralKeySpecial_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralKeySpecial_ifc.java.xml "-a:@filename="GralKeySpecial_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :130
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetHelper.java.xml goto :132
  call zbnfjax zbnf2xml -i:../org/vishia/gral/base/GralWidgetHelper.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetHelper.java.xml "-a:@filename="GralWidgetHelper"" --report:%TMP_ZBNFJAX%/org/vishia/gral/base/.zbnf.rpt
  :132
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgBuilder.java.xml goto :134
  call zbnfjax zbnf2xml -i:../org/vishia/gral/cfg/GralCfgBuilder.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgBuilder.java.xml "-a:@filename="GralCfgBuilder"" --report:%TMP_ZBNFJAX%/org/vishia/gral/cfg/.zbnf.rpt
  :134
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgData.java.xml goto :136
  call zbnfjax zbnf2xml -i:../org/vishia/gral/cfg/GralCfgData.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgData.java.xml "-a:@filename="GralCfgData"" --report:%TMP_ZBNFJAX%/org/vishia/gral/cfg/.zbnf.rpt
  :136
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgDesigner.java.xml goto :138
  call zbnfjax zbnf2xml -i:../org/vishia/gral/cfg/GralCfgDesigner.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgDesigner.java.xml "-a:@filename="GralCfgDesigner"" --report:%TMP_ZBNFJAX%/org/vishia/gral/cfg/.zbnf.rpt
  :138
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgWriter.java.xml goto :140
  call zbnfjax zbnf2xml -i:../org/vishia/gral/cfg/GralCfgWriter.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgWriter.java.xml "-a:@filename="GralCfgWriter"" --report:%TMP_ZBNFJAX%/org/vishia/gral/cfg/.zbnf.rpt
  :140
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgZbnf.java.xml goto :142
  call zbnfjax zbnf2xml -i:../org/vishia/gral/cfg/GralCfgZbnf.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgZbnf.java.xml "-a:@filename="GralCfgZbnf"" --report:%TMP_ZBNFJAX%/org/vishia/gral/cfg/.zbnf.rpt
  :142
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/cfg/package-info.java.xml goto :144
  call zbnfjax zbnf2xml -i:../org/vishia/gral/cfg/package-info.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/cfg/package-info.java.xml "-a:@filename="package-info"" --report:%TMP_ZBNFJAX%/org/vishia/gral/cfg/.zbnf.rpt
  :144
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgElement.java.xml goto :146
  call zbnfjax zbnf2xml -i:../org/vishia/gral/cfg/GralCfgElement.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgElement.java.xml "-a:@filename="GralCfgElement"" --report:%TMP_ZBNFJAX%/org/vishia/gral/cfg/.zbnf.rpt
  :146
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgPanel.java.xml goto :148
  call zbnfjax zbnf2xml -i:../org/vishia/gral/cfg/GralCfgPanel.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgPanel.java.xml "-a:@filename="GralCfgPanel"" --report:%TMP_ZBNFJAX%/org/vishia/gral/cfg/.zbnf.rpt
  :148
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgPosition.java.xml goto :150
  call zbnfjax zbnf2xml -i:../org/vishia/gral/cfg/GralCfgPosition.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgPosition.java.xml "-a:@filename="GralCfgPosition"" --report:%TMP_ZBNFJAX%/org/vishia/gral/cfg/.zbnf.rpt
  :150
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtCanvasStorePanel.java.xml goto :152
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtCanvasStorePanel.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtCanvasStorePanel.java.xml "-a:@filename="SwtCanvasStorePanel"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :152
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/FactorySwt.java.xml goto :154
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/FactorySwt.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/FactorySwt.java.xml "-a:@filename="FactorySwt"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :154
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtFileDialog.java.xml goto :156
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtFileDialog.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtFileDialog.java.xml "-a:@filename="SwtFileDialog"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :156
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtFocusAction.java.xml goto :158
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtFocusAction.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtFocusAction.java.xml "-a:@filename="SwtFocusAction"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :158
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGridPanel.java.xml goto :160
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtGridPanel.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGridPanel.java.xml "-a:@filename="SwtGridPanel"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :160
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtImage.java.xml goto :162
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtImage.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtImage.java.xml "-a:@filename="SwtImage"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :162
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtMng.java.xml goto :164
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtMng.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtMng.java.xml "-a:@filename="SwtMng"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :164
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtLed.java.xml goto :166
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtLed.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtLed.java.xml "-a:@filename="SwtLed"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :166
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/package-info.java.xml goto :168
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/package-info.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/package-info.java.xml "-a:@filename="package-info"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :168
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtHtmlBox.java.xml goto :170
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtHtmlBox.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtHtmlBox.java.xml "-a:@filename="SwtHtmlBox"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :170
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtKeyListener.java.xml goto :172
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtKeyListener.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtKeyListener.java.xml "-a:@filename="SwtKeyListener"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :172
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTransparentLabel.java.xml goto :174
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtTransparentLabel.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTransparentLabel.java.xml "-a:@filename="SwtTransparentLabel"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :174
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtWidgetSimpleWrapper.java.xml goto :176
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtWidgetSimpleWrapper.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtWidgetSimpleWrapper.java.xml "-a:@filename="SwtWidgetSimpleWrapper"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :176
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGralKey.java.xml goto :178
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtGralKey.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGralKey.java.xml "-a:@filename="SwtGralKey"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :178
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtPrimaryWindow.java.xml goto :180
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtPrimaryWindow.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtPrimaryWindow.java.xml "-a:@filename="SwtPrimaryWindow"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :180
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTextFieldWrapper.java.xml goto :182
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtTextFieldWrapper.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTextFieldWrapper.java.xml "-a:@filename="SwtTextFieldWrapper"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :182
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtCurveView.java.xml goto :184
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtCurveView.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtCurveView.java.xml "-a:@filename="SwtCurveView"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :184
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtWidgetHelper.java.xml goto :186
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtWidgetHelper.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtWidgetHelper.java.xml "-a:@filename="SwtWidgetHelper"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :186
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtButton.java.xml goto :188
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtButton.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtButton.java.xml "-a:@filename="SwtButton"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :188
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGralMouseListener.java.xml goto :190
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtGralMouseListener.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGralMouseListener.java.xml "-a:@filename="SwtGralMouseListener"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :190
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGraphicThread.java.xml goto :192
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtGraphicThread.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGraphicThread.java.xml "-a:@filename="SwtGraphicThread"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :192
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtCanvasDrawer.java.xml goto :194
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtCanvasDrawer.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtCanvasDrawer.java.xml "-a:@filename="SwtCanvasDrawer"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :194
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtWidgetMenu.java.xml goto :196
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtWidgetMenu.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtWidgetMenu.java.xml "-a:@filename="SwtWidgetMenu"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :196
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtDragListener.java.xml goto :198
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtDragListener.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtDragListener.java.xml "-a:@filename="SwtDragListener"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :198
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtDropListener.java.xml goto :200
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtDropListener.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtDropListener.java.xml "-a:@filename="SwtDropListener"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :200
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTextPanel.java.xml goto :202
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtTextPanel.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTextPanel.java.xml "-a:@filename="SwtTextPanel"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :202
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtPanel.java.xml goto :204
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtPanel.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtPanel.java.xml "-a:@filename="SwtPanel"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :204
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtProperties.java.xml goto :206
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtProperties.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtProperties.java.xml "-a:@filename="SwtProperties"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :206
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtMenu.java.xml goto :208
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtMenu.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtMenu.java.xml "-a:@filename="SwtMenu"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :208
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtSetValue_ifc.java.xml goto :210
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtSetValue_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtSetValue_ifc.java.xml "-a:@filename="SwtSetValue_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :210
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTable.java.xml goto :212
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtTable.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTable.java.xml "-a:@filename="SwtTable"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :212
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTabbedPanel.java.xml goto :214
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtTabbedPanel.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTabbedPanel.java.xml "-a:@filename="SwtTabbedPanel"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :214
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtValueBar.java.xml goto :216
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtValueBar.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtValueBar.java.xml "-a:@filename="SwtValueBar"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :216
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTextBox.java.xml goto :218
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtTextBox.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTextBox.java.xml "-a:@filename="SwtTextBox"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :218
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/swt/SwtSubWindow.java.xml goto :220
  call zbnfjax zbnf2xml -i:../org/vishia/gral/swt/SwtSubWindow.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtSubWindow.java.xml "-a:@filename="SwtSubWindow"" --report:%TMP_ZBNFJAX%/org/vishia/gral/swt/.zbnf.rpt
  :220
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/widget/GralCommandSelector.java.xml goto :222
  call zbnfjax zbnf2xml -i:../org/vishia/gral/widget/GralCommandSelector.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/widget/GralCommandSelector.java.xml "-a:@filename="GralCommandSelector"" --report:%TMP_ZBNFJAX%/org/vishia/gral/widget/.zbnf.rpt
  :222
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/widget/package-info.java.xml goto :224
  call zbnfjax zbnf2xml -i:../org/vishia/gral/widget/package-info.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/widget/package-info.java.xml "-a:@filename="package-info"" --report:%TMP_ZBNFJAX%/org/vishia/gral/widget/.zbnf.rpt
  :224
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/widget/GralFileSelector.java.xml goto :226
  call zbnfjax zbnf2xml -i:../org/vishia/gral/widget/GralFileSelector.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/widget/GralFileSelector.java.xml "-a:@filename="GralFileSelector"" --report:%TMP_ZBNFJAX%/org/vishia/gral/widget/.zbnf.rpt
  :226
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/widget/GralHtmlWindow.java.xml goto :228
  call zbnfjax zbnf2xml -i:../org/vishia/gral/widget/GralHtmlWindow.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/widget/GralHtmlWindow.java.xml "-a:@filename="GralHtmlWindow"" --report:%TMP_ZBNFJAX%/org/vishia/gral/widget/.zbnf.rpt
  :228
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/widget/GralInfoBox.java.xml goto :230
  call zbnfjax zbnf2xml -i:../org/vishia/gral/widget/GralInfoBox.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/widget/GralInfoBox.java.xml "-a:@filename="GralInfoBox"" --report:%TMP_ZBNFJAX%/org/vishia/gral/widget/.zbnf.rpt
  :230
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/widget/GralSelectList.java.xml goto :232
  call zbnfjax zbnf2xml -i:../org/vishia/gral/widget/GralSelectList.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/widget/GralSelectList.java.xml "-a:@filename="GralSelectList"" --report:%TMP_ZBNFJAX%/org/vishia/gral/widget/.zbnf.rpt
  :232
  
  if exist %TMP_ZBNFJAX%/org/vishia/gral/widget/GralSwitchExclusiveButtonMng.java.xml goto :234
  call zbnfjax zbnf2xml -i:../org/vishia/gral/widget/GralSwitchExclusiveButtonMng.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/gral/widget/GralSwitchExclusiveButtonMng.java.xml "-a:@filename="GralSwitchExclusiveButtonMng"" --report:%TMP_ZBNFJAX%/org/vishia/gral/widget/.zbnf.rpt
  :234
  
  if exist %TMP_ZBNFJAX%/org/vishia/guiInspc/InspcGui.java.xml goto :236
  call zbnfjax zbnf2xml -i:../org/vishia/guiInspc/InspcGui.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/guiInspc/InspcGui.java.xml "-a:@filename="InspcGui"" --report:%TMP_ZBNFJAX%/org/vishia/guiInspc/.zbnf.rpt
  :236
  
  if exist %TMP_ZBNFJAX%/org/vishia/guiInspc/InspcCurveView.java.xml goto :238
  call zbnfjax zbnf2xml -i:../org/vishia/guiInspc/InspcCurveView.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/guiInspc/InspcCurveView.java.xml "-a:@filename="InspcCurveView"" --report:%TMP_ZBNFJAX%/org/vishia/guiInspc/.zbnf.rpt
  :238
  
  if exist %TMP_ZBNFJAX%/org/vishia/guiInspc/InspcGuiFieldsFromFile.java.xml goto :240
  call zbnfjax zbnf2xml -i:../org/vishia/guiInspc/InspcGuiFieldsFromFile.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/guiInspc/InspcGuiFieldsFromFile.java.xml "-a:@filename="InspcGuiFieldsFromFile"" --report:%TMP_ZBNFJAX%/org/vishia/guiInspc/.zbnf.rpt
  :240
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdCopyCmd.java.xml goto :242
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdCopyCmd.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdCopyCmd.java.xml "-a:@filename="FcmdCopyCmd"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :242
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdDelete.java.xml goto :244
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdDelete.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdDelete.java.xml "-a:@filename="FcmdDelete"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :244
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdEdit.java.xml goto :246
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdEdit.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdEdit.java.xml "-a:@filename="FcmdEdit"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :246
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdFilesCp.java.xml goto :248
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdFilesCp.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdFilesCp.java.xml "-a:@filename="FcmdFilesCp"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :248
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdFileCard.java.xml goto :250
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdFileCard.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdFileCard.java.xml "-a:@filename="FcmdFileCard"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :250
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdExecuter.java.xml goto :252
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdExecuter.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdExecuter.java.xml "-a:@filename="FcmdExecuter"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :252
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdTargetProxy.java.xml goto :254
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdTargetProxy.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdTargetProxy.java.xml "-a:@filename="FcmdTargetProxy"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :254
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdFavorCard.java.xml goto :256
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdFavorCard.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdFavorCard.java.xml "-a:@filename="FcmdFavorCard"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :256
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdFileProps.java.xml goto :258
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdFileProps.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdFileProps.java.xml "-a:@filename="FcmdFileProps"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :258
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdMkDirFile.java.xml goto :260
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdMkDirFile.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdMkDirFile.java.xml "-a:@filename="FcmdMkDirFile"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :260
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdView.java.xml goto :262
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdView.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdView.java.xml "-a:@filename="FcmdView"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :262
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdStatusLine.java.xml goto :264
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdStatusLine.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdStatusLine.java.xml "-a:@filename="FcmdStatusLine"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :264
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/package-info.java.xml goto :266
  call zbnfjax zbnf2xml -i:../org/vishia/commander/package-info.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/package-info.java.xml "-a:@filename="package-info"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :266
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdLeftMidRightPanel.java.xml goto :268
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdLeftMidRightPanel.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdLeftMidRightPanel.java.xml "-a:@filename="FcmdLeftMidRightPanel"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :268
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdWidgetNames.java.xml goto :270
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdWidgetNames.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdWidgetNames.java.xml "-a:@filename="FcmdWidgetNames"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :270
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdFavorPathSelector.java.xml goto :272
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdFavorPathSelector.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdFavorPathSelector.java.xml "-a:@filename="FcmdFavorPathSelector"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :272
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdWindowMng.java.xml goto :274
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdWindowMng.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdWindowMng.java.xml "-a:@filename="FcmdWindowMng"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :274
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/Fcmd.java.xml goto :276
  call zbnfjax zbnf2xml -i:../org/vishia/commander/Fcmd.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/Fcmd.java.xml "-a:@filename="Fcmd"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :276
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdButtons.java.xml goto :278
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdButtons.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdButtons.java.xml "-a:@filename="FcmdButtons"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :278
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdIdents.java.xml goto :280
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdIdents.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdIdents.java.xml "-a:@filename="FcmdIdents"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :280
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdKeyActions.java.xml goto :282
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdKeyActions.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdKeyActions.java.xml "-a:@filename="FcmdKeyActions"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :282
  
  if exist %TMP_ZBNFJAX%/org/vishia/commander/FcmdDocuSearch.java.xml goto :284
  call zbnfjax zbnf2xml -i:../org/vishia/commander/FcmdDocuSearch.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/commander/FcmdDocuSearch.java.xml "-a:@filename="FcmdDocuSearch"" --report:%TMP_ZBNFJAX%/org/vishia/commander/.zbnf.rpt
  :284
  
  if exist %TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessCheckerRxTelg.java.xml goto :286
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaRun/org/vishia/inspectorAccessor/InspcAccessCheckerRxTelg.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessCheckerRxTelg.java.xml "-a:@filename="InspcAccessCheckerRxTelg"" --report:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/.zbnf.rpt
  :286
  
  if exist %TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessEvaluatorRxTelg.java.xml goto :288
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaRun/org/vishia/inspectorAccessor/InspcAccessEvaluatorRxTelg.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessEvaluatorRxTelg.java.xml "-a:@filename="InspcAccessEvaluatorRxTelg"" --report:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/.zbnf.rpt
  :288
  
  if exist %TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessExecAnswerTelg_ifc.java.xml goto :290
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaRun/org/vishia/inspectorAccessor/InspcAccessExecAnswerTelg_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessExecAnswerTelg_ifc.java.xml "-a:@filename="InspcAccessExecAnswerTelg_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/.zbnf.rpt
  :290
  
  if exist %TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessExecRxOrder_ifc.java.xml goto :292
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaRun/org/vishia/inspectorAccessor/InspcAccessExecRxOrder_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessExecRxOrder_ifc.java.xml "-a:@filename="InspcAccessExecRxOrder_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/.zbnf.rpt
  :292
  
  if exist %TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessGenerateOrder.java.xml goto :294
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaRun/org/vishia/inspectorAccessor/InspcAccessGenerateOrder.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessGenerateOrder.java.xml "-a:@filename="InspcAccessGenerateOrder"" --report:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/.zbnf.rpt
  :294
  
  if exist %TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessor.java.xml goto :296
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaRun/org/vishia/inspectorAccessor/InspcAccessor.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessor.java.xml "-a:@filename="InspcAccessor"" --report:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/.zbnf.rpt
  :296
  
  if exist %TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcMng.java.xml goto :298
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaRun/org/vishia/inspectorAccessor/InspcMng.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcMng.java.xml "-a:@filename="InspcMng"" --report:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/.zbnf.rpt
  :298
  
  if exist %TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcVariable.java.xml goto :300
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaRun/org/vishia/inspectorAccessor/InspcVariable.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcVariable.java.xml "-a:@filename="InspcVariable"" --report:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/.zbnf.rpt
  :300
  
  if exist %TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcPlugUser_ifc.java.xml goto :302
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaRun/org/vishia/inspectorAccessor/InspcPlugUser_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcPlugUser_ifc.java.xml "-a:@filename="InspcPlugUser_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/.zbnf.rpt
  :302
  
  if exist %TMP_ZBNFJAX%/org/vishia/inspectorAccessor/UserInspcPlug_ifc.java.xml goto :304
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaRun/org/vishia/inspectorAccessor/UserInspcPlug_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/UserInspcPlug_ifc.java.xml "-a:@filename="UserInspcPlug_ifc"" --report:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/.zbnf.rpt
  :304
  
  if exist %TMP_ZBNFJAX%/org/vishia/inspectorAccessor/TestAccessor.java.xml goto :306
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaRun/org/vishia/inspectorAccessor/TestAccessor.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/TestAccessor.java.xml "-a:@filename="TestAccessor"" --report:%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/.zbnf.rpt
  :306
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileWriter.java.xml goto :308
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/FileWriter.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileWriter.java.xml "-a:@filename="FileWriter"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :308
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/IndexMultiTableInteger.java.xml goto :310
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/IndexMultiTableInteger.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/IndexMultiTableInteger.java.xml "-a:@filename="IndexMultiTableInteger"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :310
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Java4C.java.xml goto :312
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/Java4C.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Java4C.java.xml "-a:@filename="Java4C"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :312
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/KeyCode.java.xml goto :314
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/KeyCode.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/KeyCode.java.xml "-a:@filename="KeyCode"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :314
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/LeapSeconds.java.xml goto :316
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/LeapSeconds.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/LeapSeconds.java.xml "-a:@filename="LeapSeconds"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :316
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/MinMaxTime.java.xml goto :318
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/MinMaxTime.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/MinMaxTime.java.xml "-a:@filename="MinMaxTime"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :318
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Assert.java.xml goto :320
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/Assert.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Assert.java.xml "-a:@filename="Assert"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :320
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/CalculatorExpr.java.xml goto :322
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/CalculatorExpr.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/CalculatorExpr.java.xml "-a:@filename="CalculatorExpr"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :322
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/CompleteConstructionAndStart.java.xml goto :324
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/CompleteConstructionAndStart.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/CompleteConstructionAndStart.java.xml "-a:@filename="CompleteConstructionAndStart"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :324
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Event.java.xml goto :326
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/Event.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Event.java.xml "-a:@filename="Event"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :326
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/EventConsumer.java.xml goto :328
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/EventConsumer.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/EventConsumer.java.xml "-a:@filename="EventConsumer"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :328
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/EventOwner.java.xml goto :330
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/EventOwner.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/EventOwner.java.xml "-a:@filename="EventOwner"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :330
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/EventThread.java.xml goto :332
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/EventThread.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/EventThread.java.xml "-a:@filename="EventThread"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :332
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileCompare.java.xml goto :334
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/FileCompare.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileCompare.java.xml "-a:@filename="FileCompare"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :334
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileRemote.java.xml goto :336
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/FileRemote.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileRemote.java.xml "-a:@filename="FileRemote"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :336
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileRemoteAccessor.java.xml goto :338
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/FileRemoteAccessor.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileRemoteAccessor.java.xml "-a:@filename="FileRemoteAccessor"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :338
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileRemoteAccessorLocalFile.java.xml goto :340
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/FileRemoteAccessorLocalFile.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileRemoteAccessorLocalFile.java.xml "-a:@filename="FileRemoteAccessorLocalFile"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :340
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileSystem.java.xml goto :342
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/FileSystem.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileSystem.java.xml "-a:@filename="FileSystem"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :342
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/ObjectValue.java.xml goto :344
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/ObjectValue.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/ObjectValue.java.xml "-a:@filename="ObjectValue"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :344
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/package-info.java.xml goto :346
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/package-info.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/package-info.java.xml "-a:@filename="package-info"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :346
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Removeable.java.xml goto :348
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/Removeable.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Removeable.java.xml "-a:@filename="Removeable"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :348
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SelectMask.java.xml goto :350
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/SelectMask.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SelectMask.java.xml "-a:@filename="SelectMask"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :350
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SelectMask_ifc.java.xml goto :352
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/SelectMask_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SelectMask_ifc.java.xml "-a:@filename="SelectMask_ifc"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :352
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/ReadChars3Stream.java.xml goto :354
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/ReadChars3Stream.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/ReadChars3Stream.java.xml "-a:@filename="ReadChars3Stream"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :354
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Sort.java.xml goto :356
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/Sort.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Sort.java.xml "-a:@filename="Sort"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :356
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedList.java.xml goto :358
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/SortedList.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedList.java.xml "-a:@filename="SortedList"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :358
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedStringList.java.xml goto :360
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/SortedStringList.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedStringList.java.xml "-a:@filename="SortedStringList"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :360
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedTree.java.xml goto :362
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/SortedTree.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedTree.java.xml "-a:@filename="SortedTree"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :362
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedTreeNode.java.xml goto :364
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/SortedTreeNode.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedTreeNode.java.xml "-a:@filename="SortedTreeNode"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :364
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SpecialCharStrings.java.xml goto :366
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/SpecialCharStrings.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SpecialCharStrings.java.xml "-a:@filename="SpecialCharStrings"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :366
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StdHexFormatWriter.java.xml goto :368
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/StdHexFormatWriter.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StdHexFormatWriter.java.xml "-a:@filename="StdHexFormatWriter"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :368
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringFormatter.java.xml goto :370
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/StringFormatter.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringFormatter.java.xml "-a:@filename="StringFormatter"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :370
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringFunctions.java.xml goto :372
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/StringFunctions.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringFunctions.java.xml "-a:@filename="StringFunctions"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :372
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringPart.java.xml goto :374
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/StringPart.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringPart.java.xml "-a:@filename="StringPart"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :374
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringPartFromFile.java.xml goto :376
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/StringPartFromFile.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringPartFromFile.java.xml "-a:@filename="StringPartFromFile"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :376
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringPartFromFileLines.java.xml goto :378
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/StringPartFromFileLines.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringPartFromFileLines.java.xml "-a:@filename="StringPartFromFileLines"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :378
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/ThreadContext.java.xml goto :380
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/ThreadContext.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/ThreadContext.java.xml "-a:@filename="ThreadContext"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :380
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Docu_UML_simpleNotation.java.xml goto :382
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/util/Docu_UML_simpleNotation.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Docu_UML_simpleNotation.java.xml "-a:@filename="Docu_UML_simpleNotation"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/.zbnf.rpt
  :382
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccess.java.xml goto :384
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccess.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccess.java.xml "-a:@filename="ByteDataAccess"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :384
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccessDbg.java.xml goto :386
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccessDbg.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccessDbg.java.xml "-a:@filename="ByteDataAccessDbg"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :386
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataSymbolicAccess.java.xml goto :388
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/ByteDataSymbolicAccess.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataSymbolicAccess.java.xml "-a:@filename="ByteDataSymbolicAccess"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :388
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/Class_Jc.java.xml goto :390
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/Class_Jc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/Class_Jc.java.xml "-a:@filename="Class_Jc"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :390
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/Field_Jc.java.xml goto :392
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/Field_Jc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/Field_Jc.java.xml "-a:@filename="Field_Jc"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :392
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/Object_Jc.java.xml goto :394
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/Object_Jc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/Object_Jc.java.xml "-a:@filename="Object_Jc"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :394
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ObjectArray_Jc.java.xml goto :396
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/ObjectArray_Jc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ObjectArray_Jc.java.xml "-a:@filename="ObjectArray_Jc"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :396
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccessSimple.java.xml goto :398
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccessSimple.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccessSimple.java.xml "-a:@filename="ByteDataAccessSimple"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :398
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/VariableAccessWithIdx.java.xml goto :400
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/VariableAccessWithIdx.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/VariableAccessWithIdx.java.xml "-a:@filename="VariableAccessWithIdx"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :400
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/package-info.java.xml goto :402
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/package-info.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/package-info.java.xml "-a:@filename="package-info"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :402
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/PositionElementInStruct.java.xml goto :404
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/PositionElementInStruct.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/PositionElementInStruct.java.xml "-a:@filename="PositionElementInStruct"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :404
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/RawDataAccess.java.xml goto :406
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/RawDataAccess.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/RawDataAccess.java.xml "-a:@filename="RawDataAccess"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :406
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/VariableAccess_ifc.java.xml goto :408
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/VariableAccess_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/VariableAccess_ifc.java.xml "-a:@filename="VariableAccess_ifc"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :408
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/VariableContainer_ifc.java.xml goto :410
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/VariableContainer_ifc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/VariableContainer_ifc.java.xml "-a:@filename="VariableContainer_ifc"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :410
  
  if exist %TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ObjectArraySet_Jc.java.xml goto :412
  call zbnfjax zbnf2xml -i:../../srcJava_vishiaBase/org/vishia/byteData/ObjectArraySet_Jc.java -s:%ZBNFJAX_HOME%/zbnf/Java2C.zbnf -y:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ObjectArraySet_Jc.java.xml "-a:@filename="ObjectArraySet_Jc"" --report:%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/.zbnf.rpt
  :412
  REM All parsed headers can transform via XSLT-2 using saxon in 2 steps.
echo building XMI
echo --rlevel=333 >%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCanvasStorage.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralColor.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralFileDialog_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTextFieldUser_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWindow_getifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralFactory_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralImageBase.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralMngBuild_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralMng_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralRectangle.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralSetValue_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPlugUser_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/package-info.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralVisibleWidgets_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPrimaryWindow_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTextField_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWindowMng_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCanvas_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralFont.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralMngApplAdapter_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCurveViewTrack_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralCurveView_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPoint.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWindow_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWidgetCfg_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralPlugUser2Gral_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTable_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTableLine_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralTextBox_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralUserAction.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/ifc/GralWidget_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/area9/GuiCfg.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/area9/GuiCallingArgs.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/area9/GralArea9MainCmd.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/area9/package-info.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/area9/GralArea9Window.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/area9/GralArea9_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GetGralWidget_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralMenu.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralWindow_setifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralDispatchCallbackWorker.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/package-info.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralGraphicThread.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralWindow.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralTextBox.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralTextField.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralButton.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralMouseWidgetAction_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralLed.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralKeyListener.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralHtmlBox.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralPos.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetChangeRequ.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralCurveView.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralPanelActivated_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralPanelContent.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralGridProperties.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetGthreadSet_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralTabbedPanel.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralValueBar.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralWidget.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralTable.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetMng.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralKeySpecial_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/base/GralWidgetHelper.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgBuilder.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgData.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgDesigner.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgWriter.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgZbnf.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/cfg/package-info.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgElement.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgPanel.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/cfg/GralCfgPosition.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtCanvasStorePanel.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/FactorySwt.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtFileDialog.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtFocusAction.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGridPanel.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtImage.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtMng.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtLed.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/package-info.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtHtmlBox.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtKeyListener.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTransparentLabel.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtWidgetSimpleWrapper.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGralKey.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtPrimaryWindow.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTextFieldWrapper.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtCurveView.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtWidgetHelper.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtButton.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGralMouseListener.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtGraphicThread.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtCanvasDrawer.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtWidgetMenu.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtDragListener.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtDropListener.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTextPanel.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtPanel.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtProperties.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtMenu.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtSetValue_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTable.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTabbedPanel.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtValueBar.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtTextBox.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/swt/SwtSubWindow.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/widget/GralCommandSelector.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/widget/package-info.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/widget/GralFileSelector.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/widget/GralHtmlWindow.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/widget/GralInfoBox.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/widget/GralSelectList.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/gral/widget/GralSwitchExclusiveButtonMng.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/guiInspc/InspcGui.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/guiInspc/InspcCurveView.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/guiInspc/InspcGuiFieldsFromFile.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdCopyCmd.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdDelete.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdEdit.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdFilesCp.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdFileCard.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdExecuter.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdTargetProxy.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdFavorCard.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdFileProps.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdMkDirFile.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdView.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdStatusLine.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/package-info.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdLeftMidRightPanel.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdWidgetNames.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdFavorPathSelector.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdWindowMng.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/Fcmd.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdButtons.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdIdents.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdKeyActions.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/commander/FcmdDocuSearch.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessCheckerRxTelg.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessEvaluatorRxTelg.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessExecAnswerTelg_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessExecRxOrder_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessGenerateOrder.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcAccessor.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcMng.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcVariable.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/InspcPlugUser_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/UserInspcPlug_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/org/vishia/inspectorAccessor/TestAccessor.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileWriter.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/IndexMultiTableInteger.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Java4C.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/KeyCode.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/LeapSeconds.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/MinMaxTime.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Assert.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/CalculatorExpr.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/CompleteConstructionAndStart.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Event.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/EventConsumer.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/EventOwner.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/EventThread.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileCompare.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileRemote.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileRemoteAccessor.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileRemoteAccessorLocalFile.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/FileSystem.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/ObjectValue.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/package-info.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Removeable.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SelectMask.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SelectMask_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/ReadChars3Stream.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Sort.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedList.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedStringList.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedTree.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SortedTreeNode.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/SpecialCharStrings.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StdHexFormatWriter.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringFormatter.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringFunctions.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringPart.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringPartFromFile.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/StringPartFromFileLines.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/ThreadContext.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/util/Docu_UML_simpleNotation.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccess.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccessDbg.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataSymbolicAccess.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/Class_Jc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/Field_Jc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/Object_Jc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ObjectArray_Jc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ByteDataAccessSimple.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/VariableAccessWithIdx.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/package-info.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/PositionElementInStruct.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/RawDataAccess.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/VariableAccess_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/VariableContainer_ifc.java.xml >>%TMP_ZBNFJAX%\input.arg
echo -i%TMP_ZBNFJAX%/../srcJava_vishiaBase/org/vishia/byteData/ObjectArraySet_Jc.java.xml >>%TMP_ZBNFJAX%\input.arg
type %TMP_ZBNFJAX%\input.arg
echo on
%XSLT_EXE% --@%TMP_ZBNFJAX%\input.arg -t%ZBNFJAX_HOME%/xsl/Java2xmiTypes.xsl -w+ -y%TMP_ZBNFJAX%/types-xmi.xml

%XSLT_EXE% --@%TMP_ZBNFJAX%\input.arg -i%TMP_ZBNFJAX%/types-xmi.xml -t%ZBNFJAX_HOME%/xsl/gen/Java2xmi.xsl -w+ -y../rpy/gral.xmi
echo off
pause


pause
goto :ende
:error
  echo error
:ende
