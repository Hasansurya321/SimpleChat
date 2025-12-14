import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { auth } from '../services/firebase';
import styles from '../styles/authStyles';

export default function RegisterScreen({ navigation }) {
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const register = async () => {
    if (!email || !username || !password) {
      return Alert.alert('Error', 'Semua field wajib diisi');
    }

    if (password.length < 6) {
      return Alert.alert('Error', 'Password minimal 6 karakter');
    }

    try {
      // 1. Firebase Auth
      await auth().createUserWithEmailAndPassword(email, password);

      // 2. simpan session
      await AsyncStorage.setItem('username', username);
      await AsyncStorage.setItem('isLogin', 'true');

      // 3. SIMPAN KE all_users (INI KUNCI UTAMA)
      const raw = await AsyncStorage.getItem('all_users');
      const allUsers = raw ? JSON.parse(raw) : [];

      const exists = allUsers.find(u => u.username === username);

      if (!exists) {
        allUsers.push({
          id: username,
          username,
          email,
        });

        await AsyncStorage.setItem('all_users', JSON.stringify(allUsers));
      }

      Alert.alert('Sukses', 'Registrasi berhasil');

      navigation.reset({
        index: 0,
        routes: [{ name: 'Dashboard' }],
      });
    } catch (e) {
      Alert.alert('Register gagal', e.message);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Register</Text>

      <TextInput
        placeholder="Email"
        style={styles.input}
        value={email}
        onChangeText={setEmail}
        autoCapitalize="none"
      />

      <TextInput
        placeholder="Username"
        style={styles.input}
        value={username}
        onChangeText={setUsername}
        autoCapitalize="none"
      />

      <TextInput
        placeholder="Password"
        style={styles.input}
        value={password}
        onChangeText={setPassword}
        secureTextEntry
      />

      <TouchableOpacity style={styles.button} onPress={register}>
        <Text style={styles.buttonText}>REGISTER</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={() => navigation.navigate('Login')}>
        <Text style={styles.linkText}>Sudah punya akun? Login</Text>
      </TouchableOpacity>
    </View>
  );
}
