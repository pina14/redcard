"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Import the Firebase SDK for Google Cloud Functions.
const functions = require("firebase-functions");
// Import and initialize the Firebase Admin SDK.
const admin = require("firebase-admin");
admin.initializeApp();
exports.onNewUserDocument = functions.firestore
    .document('users/{userId}')
    .onCreate(snap => {
    // Get an object representing the document
    const newValue = snap.data();
    // access a particular field as you would any JS property
    const name = newValue.referredBy;
    if (name !== null) {
        // Then return a promise of a set operation to update the count
        snap.ref.set({
            balance: snap.get("balance") + 500,
        }, { merge: true }).catch(error => {
            console.log(error);
        });
        const userReferring = admin.firestore().collection("users").doc(name);
        return userReferring.get().then(user => {
            const balance = user.get("balance");
            userReferring.update({
                balance: balance + 500
            }).catch(error => {
                console.log("Could not update user referring balance!");
            });
        }).catch(error => {
            console.log("Error getting user referring!");
        });
    }
    return null;
});
exports.deleteUserWhenAuthIsDeleted = functions.auth
    .user().onDelete(user => {
    return admin.firestore().collection("users").where("email", "==", user.email).limit(1).get().then(querySnap => {
        const snap = querySnap.docs[0];
        if (snap.exists) {
            snap.ref.delete().then(s => {
                console.log(`Deleted user ${user.email} from firestore!`);
            }).catch(error => {
                console.log(`Error deleting user ${user.email} from firestore!`);
            });
        }
    }).catch(error => {
        console.log(`Error getting user ${user.email} to delete it from firestore!`);
    });
});
//# sourceMappingURL=index.js.map