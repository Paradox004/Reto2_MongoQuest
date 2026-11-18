/*
 * datos_prueba.js: crea la base de datos TonguelyDB con datos de prueba.
 *
 * Uso (con MongoDB arrancado en localhost:27017):
 *     mongosh --file bd/datos_prueba.js
 *
 * ATENCIÓN: borra y vuelve a crear las colecciones de TonguelyDB.
 * Úsalo solo en la base de datos local de desarrollo.
 *
 * Los nombres de los campos coinciden con los POJO de tonguely.modelo.
 * Los campos int de Java se guardan con NumberInt(): mongosh guarda los
 * números como double por defecto y el codec POJO no podría leerlos.
 *
 * Usuarios de prueba (contraseña 1234 en todos):
 *     Profesores (panel de administración): laura@tonguely.com, iker@tonguely.com
 *     Estudiantes:                          ane@gmail.com, jon@gmail.com
 */

db = db.getSiblingDB("TonguelyDB");

// 1. Empezar de cero
["Usuario", "Cursos", "Profesores", "Inscripciones", "Actividades"].forEach(function (c) {
    db.getCollection(c).drop();
});

// 2. Colecciones con validación $jsonSchema (IL5.4)
db.createCollection("Usuario", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["nombre", "email", "contraseña", "racha"],
            properties: {
                nombre: { bsonType: "string", minLength: 1, description: "obligatorio" },
                email: {
                    bsonType: "string",
                    pattern: "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$",
                    description: "obligatorio y con formato de email"
                },
                "contraseña": { bsonType: "string", minLength: 4, description: "mínimo 4 caracteres" },
                racha: { bsonType: "int", minimum: 0, description: "entero mayor o igual que 0" },
                cursos: { bsonType: "array" }
            }
        }
    }
});
db.Usuario.createIndex({ email: 1 }, { unique: true });

db.createCollection("Cursos", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["nombre", "precio"],
            properties: {
                nombre: { bsonType: "string", minLength: 1 },
                precio: { bsonType: "int", minimum: 0 },
                descripcion: { bsonType: "string" },
                foto: { bsonType: "string" },
                profesores: { bsonType: "array" }
            }
        }
    }
});
db.Cursos.createIndex({ nombre: 1 }, { unique: true });

// 3. Profesores
var laura = db.Profesores.insertOne({
    nombre: "Laura", apellidos: "Martín", email: "laura@tonguely.com", "contraseña": "1234",
    sueldo: NumberInt(1800), horasTrabajo: NumberInt(30),
    cursos: [{ nombre: "Inglés A1" }, { nombre: "Francés A1" }]
}).insertedId;

var iker = db.Profesores.insertOne({
    nombre: "Iker", apellidos: "Etxeberria", email: "iker@tonguely.com", "contraseña": "1234",
    sueldo: NumberInt(1700), horasTrabajo: NumberInt(25),
    cursos: [{ nombre: "Alemán A1" }]
}).insertedId;

// 4. Cursos (los profesores van embebidos con nombre, apellidos y email)
var ingles = db.Cursos.insertOne({
    nombre: "Inglés A1", precio: NumberInt(0), foto: "/img/ingles.jpg",
    descripcion: "Primeros pasos en inglés: saludos, presentarse y vocabulario básico.",
    profesores: [{ nombre: "Laura", apellidos: "Martín", email: "laura@tonguely.com" }]
}).insertedId;

var frances = db.Cursos.insertOne({
    nombre: "Francés A1", precio: NumberInt(15), foto: "/img/frances.jpg",
    descripcion: "Francés desde cero: los números, los colores y la familia.",
    profesores: [{ nombre: "Laura", apellidos: "Martín", email: "laura@tonguely.com" }]
}).insertedId;

var aleman = db.Cursos.insertOne({
    nombre: "Alemán A1", precio: NumberInt(20), foto: "/img/aleman.jpg",
    descripcion: "Alemán básico: artículos, saludos y frases del día a día.",
    profesores: [{ nombre: "Iker", apellidos: "Etxeberria", email: "iker@tonguely.com" }]
}).insertedId;

// 5. Estudiantes
var ane = db.Usuario.insertOne({
    nombre: "Ane", email: "ane@gmail.com", "contraseña": "1234", racha: NumberInt(3), cursos: []
}).insertedId;

var jon = db.Usuario.insertOne({
    nombre: "Jon", email: "jon@gmail.com", "contraseña": "1234", racha: NumberInt(0), cursos: []
}).insertedId;

// 6. Inscripciones
db.Inscripciones.insertMany([
    { idEstudiante: ane, idCurso: ingles, progreso: NumberInt(40), fechaInicio: new Date("2026-10-01"), estado: "En curso" },
    { idEstudiante: ane, idCurso: frances, progreso: NumberInt(0), fechaInicio: new Date("2026-11-10"), estado: "En curso" },
    { idEstudiante: jon, idCurso: aleman, progreso: NumberInt(10), fechaInicio: new Date("2026-11-05"), estado: "En curso" }
]);

// 7. Actividades
// ActividadesDAO filtra por "curso_id" y el modelo Actividades lee "idCurso":
// se guardan los dos hasta que se unifique el nombre en el DAO.
function actividad(idCurso, tipo, pregunta, opciones, respuesta) {
    return { idCurso: idCurso, curso_id: idCurso, tipo: tipo, pregunta: pregunta,
             opciones: opciones, respuesta_correcta: respuesta };
}

db.Actividades.insertMany([
    actividad(ingles, "test", "¿Cómo se dice «hola» en inglés?", ["Hello", "Bye", "Thanks"], "Hello"),
    actividad(ingles, "pregunta", "Traduce al inglés: «gato»", [], "cat"),
    actividad(ingles, "test", "Elige la frase correcta:", ["I is happy", "I am happy", "I are happy"], "I am happy"),
    actividad(frances, "test", "¿Cómo se dice «gracias» en francés?", ["Merci", "Bonjour", "Salut"], "Merci"),
    actividad(frances, "pregunta", "Traduce al francés: «rojo»", [], "rouge"),
    actividad(aleman, "test", "¿Cuál es el artículo de «Haus»?", ["der", "die", "das"], "das"),
    actividad(aleman, "pregunta", "Traduce al alemán: «agua»", [], "Wasser")
]);

// 8. Resumen
print("TonguelyDB creada:");
["Usuario", "Cursos", "Profesores", "Inscripciones", "Actividades"].forEach(function (c) {
    print("  " + c + ": " + db.getCollection(c).countDocuments() + " documentos");
});
