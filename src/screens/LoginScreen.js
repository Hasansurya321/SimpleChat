import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, Alert } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { auth } from '../services/firebase';
import styles from '../styles/authStyles';

export default function LoginScreen({ navigation }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const login = async () => {
    if (!email) return Alert.alert('Login gagal', 'Email wajib diisi');
    if (!password) return Alert.alert('Login gagal', 'Password wajib diisi');

    try {
      await auth().signInWithEmailAndPassword(email, password);

      await AsyncStorage.setItem('isLogin', 'true');
      await AsyncStorage.setItem('email', email);

      Alert.alert('Sukses', 'Login berhasil');
      navigation.replace('Dashboard');
    } catch (e) {
      Alert.alert('Login gagal', e.message);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Login</Text>

      <TextInput
        placeholder="Email"
        style={styles.input}
        autoCapitalize="none"
        keyboardType="email-address"
        value={email}
        onChangeText={setEmail}
      />

      <TextInput
        placeholder="Password"
        style={styles.input}
        secureTextEntry
        value={password}
        onChangeText={setPassword}
      />

      <TouchableOpacity style={styles.button} onPress={login}>
        <Text style={styles.buttonText}>LOGIN</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.link}
        onPress={() => navigation.navigate('Register')}
      >
        <Text style={styles.linkText}>Belum punya akun? Register</Text>
      </TouchableOpacity>
    </View>
  );
}
