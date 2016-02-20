<?php

/**
 * @author Ravi Tamada
 * @link http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/ Complete tutorial
 */

class DB_Functions
{
    private $conn;
   
    function __construct()
    {
        require_once 'DB_Connect.php';
        $db         = new Db_Connect();
        $this->conn = $db->connect();
    }
    
    
    
    // destructor
    
    function __destruct()
    {
    }
    
    /**
     * Storing new user
     * returns user details
     */
    
    public function storeUser($name, $email, $api_key, $password)
    {
        
        $uuid = uniqid('', true);
        $encrypted_password = md5($password); // encrypted password
        $stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, api_key, encrypted_password, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("sssss", $uuid, $name, $email, $api_key, $encrypted_password);
        $result = $stmt->execute();
        $stmt->close();
        
        // check for successful store
        
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return $user;
        } else {
            return 'asfdasfasdf';
        }
    }
    
    /**
     * Get user by email and password
     */
    
    public function getUserByEmailAndPassword($email, $password)
    {
        $stmt           = $this->conn->prepare("SELECT * FROM users WHERE email = ? AND encrypted_password = ?");
        $crypt_password = md5($password);
        $stmt->bind_param("ss", $email, $crypt_password);
        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }
    }
    
    /**
     * Check user is existed or not
     */
    
    public function isUserExisted($email)
    {
        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $stmt->store_result();
        if ($stmt->num_rows > 0) {
            
            // user existed
            
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }
    
    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    
    public function hashSSHA($password)
    {
        $salt      = sha1(rand());
        $salt      = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash      = array(
            "salt" => $salt,
            "encrypted" => $encrypted
        );
        return $hash;
    }
    
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    
    public function checkhashSSHA($salt, $password)
    {
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
        return $hash;
    }
}
?>