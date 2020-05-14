const functions = require("firebase-functions");

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require("firebase-admin");
admin.initializeApp();
const db = admin.firestore();

exports.createNotification = functions.firestore
  .document("/complaints/{id}")
  .onUpdate((change) => {
    return db
      .collection("notifications")
      .add({
        timestamp: new Date(),
        user: change.after.data().email,
        status: change.after.data().status,
        category: change.after.data().category,
        location: change.after.data().location,
        description: change.after.data().description,
      })
      .then((ref) => {
        console.log("Added notification with ID: ", ref.id);
      });
  });
