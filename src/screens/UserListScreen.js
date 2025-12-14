import React, { useCallback, useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  FlatList,
  StyleSheet,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useFocusEffect } from '@react-navigation/native';

export default function UserListScreen({ navigation }) {
  const [users, setUsers] = useState([]);

  const loadUsers = async () => {
    const me = await AsyncStorage.getItem('username');

    const raw = await AsyncStorage.getItem('all_users');
    const allUsers = raw ? JSON.parse(raw) : [];

    const list = allUsers
      .filter(u => u.username !== me)
      .map(u => ({
        id: u.username,
        name: u.username,
      }));

    setUsers(list);
  };

  useFocusEffect(
    useCallback(() => {
      loadUsers();
    }, []),
  );

  const openChat = user => {
    navigation.navigate('ChatDetail', { user });
  };

  const renderItem = ({ item }) => (
    <TouchableOpacity style={styles.item} onPress={() => openChat(item)}>
      <Text style={styles.name}>{item.name}</Text>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <FlatList
        data={users}
        keyExtractor={item => item.id}
        renderItem={renderItem}
        ListEmptyComponent={
          <Text style={styles.empty}>Belum ada user lain</Text>
        }
      />

      <TouchableOpacity
        style={styles.backBtn}
        onPress={() => navigation.navigate('Dashboard')}
      >
        <Text>Kembali ke Dashboard</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16 },
  item: {
    padding: 14,
    borderWidth: 1,
    borderColor: '#ccc',
    marginBottom: 10,
    borderRadius: 6,
  },
  name: { fontSize: 16 },
  empty: { marginTop: 30, textAlign: 'center' },
  backBtn: {
    padding: 14,
    borderWidth: 1,
    borderColor: '#999',
    borderRadius: 6,
    alignItems: 'center',
    marginTop: 10,
  },
});
