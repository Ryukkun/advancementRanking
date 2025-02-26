if ( -not (test-path server))
{
    mkdir server
}
cd server

$pluginsId = "advancementRanking"
$mcVersion = "1.21.4"
$web = invoke-webrequest "https://api.papermc.io/v2/projects/paper/versions/${mcVersion}/builds"
$webJson = ConvertFrom-Json $web
$jarName = $webJson.builds[-1].downloads.application.name
$build = $webJson.builds[-1].build

if ( -not (test-path $jarName)) {
    Invoke-WebRequest "https://api.papermc.io/v2/projects/paper/versions/${mcVersion}/builds/${build}/downloads/${jarName}" -OutFile $jarName
}
#if (-not (Test-Path "eula.txt")) {
#    echo "`n`neula=true" | Out-File -FilePath "eula.txt"
#}
if (-not (Test-Path "plugins")) {
    mkdir "plugins"
}
copy -Path "../target/${pluginsId}.jar" -Destination "./plugins/"



java21 -Xmx3G -Xms3G -jar ${jarName} --nogui