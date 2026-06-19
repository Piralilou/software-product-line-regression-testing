@echo off
setlocal

set EVOSUITE_JAR=evosuite-1.1.0.jar
set CLASSPATH=target\${iris.product}\classes\
set PACKAGE=irisdeltaj.simplerelational.br.unb.cic.iris.command.console
set CLASSES= AbstractCommandManager AbstractDAO AbstractEmailProvider AbstractListMessagesCommand AbstractMailCommand BaseCommandManager BaseManager ChangeFolderConsoleCommand ClassFinder CommandNotFoundException Configuration ConsoleCommandManager ConsoleConnectCommand ConsoleHelpCommand ConsoleListFoldersCommand ConsoleListMessagesCommand ConsoleListProvidersCommand ConsoleListRemoteFoldersCommand ConsoleQuitCommand ConsoleSendMessageCommand ConsoleStatusCommand CurrentFolderConsoleCommand DBException DefaultProvider DownloadMessagesConsoleCommand EmailClient EmailDAO EmailException EmailMessage EmailMessageValidationException EmailSender EmailSession EmailUncheckedException EmailValidator FolderContent FolderDAO FolderManager GmailProvider HibernateUtil IrisFolder JarFileLoader LoadConsoleCommand MainProgram MessageBundle OutlookProvider ProviderManager ReadMessageConsoleCommand SQLiteDialect SimpleCommandManager StringUtil SystemFacade TestFolderDAO YahooProvider
  
 

for %%C in (%CLASSES%) do (
    echo Running EvoSuite for %PACKAGE%.%%C
    java -jar %EVOSUITE_JAR% -class %PACKAGE%.%%C -projectCP %CLASSPATH%
)

endlocal
pause