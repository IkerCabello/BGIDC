const functions = require("firebase-functions");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");
const { v4: uuidv4 } = require("uuid");

admin.initializeApp();
const db = admin.firestore();

const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: "idvkm.sender@gmail.com", // ✅ Cambia a tu cuenta segura si es necesario
    pass: "mvejdlvzjyepzqvx",           // ⚠️ Considera usar una contraseña de aplicación
  },
});

exports.sendCredentials = functions.https.onCall(async (data, context) => {
  const email = data.email;

  if (!email || typeof email !== "string") {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "The field 'email' is mandatory and it has to be a text."
    );
  }

  const username = "user_" + Math.floor(Math.random() * 100000);
  const password = uuidv4().substring(0, 8);

  const userRef = db.collection("users").doc();
  await userRef.set({
    id: userRef.id,
    name: username,
    email: email,
    password: password,
    company: "",
    position: "",
    user_type: "attendee",
    about: "",
    profile_img: "",
    sessions: [],
    sessionDetails: [],
    needsUpdate: true,
  });

  const mailOptions = {
    from: "BGIDC <idvkm.sender@gmail.com>",
    to: email,
    subject: "Tus credenciales temporales",
    text: `Hola! Aquí tienes tus credenciales:\n\nUsuario: ${username}\nContraseña: ${password}\n\nPor favor, úsalas para iniciar sesión y luego actualiza tu perfil.`,
  };

  await transporter.sendMail(mailOptions);
  return { success: true };
});