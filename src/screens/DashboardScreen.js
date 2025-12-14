import React, { useEffect, useState } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { auth } from '../services/firebase';

export default function DashboardScreen({ navigation }) {
  const [username, setUsername] = useState('');

  useEffect(() => {
    const loadUsername = async () => {
      const savedUsername = await AsyncStorage.getItem('username');
      if (savedUsername) {
        setUsername(savedUsername);
      }
    };
    loadUsername();
  }, []);

  const logout = async () => {
    await auth().signOut();
    await AsyncStorage.removeItem('isLogin');
    await AsyncStorage.removeItem('username');
    navigation.replace('Login');
  };

  return (
    <View style={styles.container}>
      <Text style={styles.welcome}>
        Selamat Datang{username ? `, ${username}` : ''}
      </Text>

      <TouchableOpacity
        style={styles.button}
        onPress={() => navigation.navigate('ChatList')}
      >
        <Text>Chat</Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={styles.button}
        onPress={() => navigation.navigate('UserList')}
      >
        <Text>Lihat User Lain</Text>
      </TouchableOpacity>

      <TouchableOpacity style={styles.logout} onPress={logout}>
        <Text style={{ color: 'white' }}>Logout</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20 },
  welcome: {
    fontSize: 22,
    textAlign: 'center',
    marginBottom: 20,
    fontWeight: 'bold',
  },
  button: {
    padding: 15,
    backgroundColor: '#ddd',
    marginBottom: 10,
    alignItems: 'center',
  },
  logout: {
    padding: 15,
    backgroundColor: 'red',
    marginTop: 20,
    alignItems: 'center',
  },
});
