const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.notifyNewPost = functions.firestore
    .document("lifePosts/{postId}") // Ensure to replace with your actual collection name
    .onCreate((snapshot, context) => {
        const newPost = snapshot.data();
        const payload = {
            notification: {
                title: `New Post in ${newPost.category}`,
                body: newPost.title,
                click_action: "FLUTTER_NOTIFICATION_CLICK",
            },
        };

        return admin.messaging().sendToTopic("new_post", payload)
            .then(response => {
                console.log("Notification sent successfully:", response);
            })
            .catch(error => {
                console.error("Error sending notification:", error);
            });
    });
