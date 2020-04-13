var service = "Email";
module.exports = {
  initialize: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, service, "initialize", []);
  },
  send: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, service, "send", [email]);
  }
};