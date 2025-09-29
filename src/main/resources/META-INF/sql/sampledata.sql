-- Insert admin user
INSERT INTO users (id, name, surname, age, password, role, esnCard, ujaEmail) VALUES (1, 'Admin', 'User',25, 'PBKDF2WithHmacSHA512:3072:wCRyLk9mFpFKUHuT5CKsuyeuN25P7tlqPkqKa1fGSKLgSLwkZKGTj+LWtIB8+sAmLf/11YdJj1da1IcToUpxEA==:frRDuoDwNAAgWBDHQ2fdfpC0NFt4IaYZ7lk7o6e2w+I=', 'admin', 'ESN000', 'admin@university.edu');

-- Insert sample students
INSERT INTO users (id, name, surname, age, password, role, esnCard, ujaEmail) VALUES (2, 'John', 'Smith',28, 'PBKDF2WithHmacSHA512:3072:wCRyLk9mFpFKUHuT5CKsuyeuN25P7tlqPkqKa1fGSKLgSLwkZKGTj+LWtIB8+sAmLf/11YdJj1da1IcToUpxEA==:frRDuoDwNAAgWBDHQ2fdfpC0NFt4IaYZ7lk7o6e2w+I=', 'student', 'ESN001', 'john@university.edu');
INSERT INTO users (id, name, surname, age, password, role, esnCard, ujaEmail) VALUES (3, 'Maria', 'Garcia',21, 'PBKDF2WithHmacSHA512:3072:wCRyLk9mFpFKUHuT5CKsuyeuN25P7tlqPkqKa1fGSKLgSLwkZKGTj+LWtIB8+sAmLf/11YdJj1da1IcToUpxEA==:frRDuoDwNAAgWBDHQ2fdfpC0NFt4IaYZ7lk7o6e2w+I=', 'student', 'ESN002', 'maria@university.edu');
INSERT INTO users (id, name, surname, age, password, role, esnCard, ujaEmail) VALUES (4, 'Oleksandr', 'Petrov',24, 'PBKDF2WithHmacSHA512:3072:wCRyLk9mFpFKUHuT5CKsuyeuN25P7tlqPkqKa1fGSKLgSLwkZKGTj+LWtIB8+sAmLf/11YdJj1da1IcToUpxEA==:frRDuoDwNAAgWBDHQ2fdfpC0NFt4IaYZ7lk7o6e2w+I=', 'student', 'ESN003', 'alex@university.edu');
INSERT INTO users (id, name, surname, age, password, role, esnCard, ujaEmail) VALUES (5, 'Sophie', 'Dubois',26, 'PBKDF2WithHmacSHA512:3072:wCRyLk9mFpFKUHuT5CKsuyeuN25P7tlqPkqKa1fGSKLgSLwkZKGTj+LWtIB8+sAmLf/11YdJj1da1IcToUpxEA==:frRDuoDwNAAgWBDHQ2fdfpC0NFt4IaYZ7lk7o6e2w+I=', 'student', 'ESN004', 'sophie@university.edu');

-- Reset sequence
ALTER TABLE users ALTER COLUMN id RESTART WITH 6;