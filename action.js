var https = require("https");

let IssueUrl = {
  init() {
    const ISSUE_URL = process.argv.slice(2)[0].replace('--url=', '');
    return (ISSUE_URL);
  }
};

main();

function main(){

  let url = IssueUrl.init()
  var options = {
  host: 'api.github.com',
  path: url,
  method: 'GET',
  headers: {'user-agent': 'node.js'}
  };

  var request = https.request(options, function(response){
  var body = '';
  response.on("data", function(chunk){
      body += chunk.toString('utf8');
  });

  response.on("end", function(){
      console.log(`"Github Link:${JSON.parse(body).html_url +'\\n\\n'+ JSON.stringify(JSON.parse(body).body).slice(1)}`);
      });
  });

  request.on('error', function(err) {
    console.log("Issue in fetching the details");
  });

  request.end();
}
    
