
class StorageDB {
    constructor(dbName = 'WeatherDB', storeName = 'WeatherMasterDataStore') {
        this.dbName = dbName;
        this.storeName = storeName;
    }

    async init() {
        return new Promise((resolve, reject) => {
            const request = indexedDB.open(this.dbName, 1);

            request.onupgradeneeded = (event) => {
                const db = event.target.result;
                if (!db.objectStoreNames.contains(this.storeName)) {
                    db.createObjectStore(this.storeName);
                }
            };

            request.onsuccess = (event) => resolve(event.target.result);
            request.onerror = (event) => reject(event.target.error);
        });
    }

    async setItem(key, value) {
        const db = await this.init();
        return new Promise((resolve, reject) => {
            const transaction = db.transaction([this.storeName], 'readwrite');
            const store = transaction.objectStore(this.storeName);
            const request = store.put(value, key);
            request.onsuccess = () => {
                this.triggerStorageChangeEvent(key);
                this.broadcastChange(key);  // Custom event triggered with key
                resolve(true);
            };
            request.onerror = (event) => reject(event.target.error);
        });
    }

    async getItem(key) {
        const db = await this.init();
        return new Promise((resolve, reject) => {
            const transaction = db.transaction([this.storeName], 'readonly');
            const store = transaction.objectStore(this.storeName);
            const request = store.get(key);
            request.onsuccess = () => resolve(request.result);
            request.onerror = (event) => reject(event.target.error);
        });
    }

    async removeItem(key) {
        const db = await this.init();
        return new Promise((resolve, reject) => {
            const transaction = db.transaction([this.storeName], 'readwrite');
            const store = transaction.objectStore(this.storeName);
            const request = store.delete(key);
            request.onsuccess = () => {
                this.triggerStorageChangeEvent(key);
                this.broadcastChange(key);  // Custom event triggered with key
                resolve(true);
            };
            request.onerror = (event) => reject(event.target.error);
        });
    }

    async clear() {
        const db = await this.init();
        return new Promise((resolve, reject) => {
            const transaction = db.transaction([this.storeName], 'readwrite');
            const store = transaction.objectStore(this.storeName);
            const request = store.clear();
            request.onsuccess = () => {
                this.triggerStorageChangeEvent(null);
                this.broadcastChange(null);
                resolve(true);
            };
            request.onerror = (event) => reject(event.target.error);
        });
    }

    triggerStorageChangeEvent(key) {
        const eventDetail = key !== null ? { key } : { key: null, message: 'store cleared' };
        const event = new CustomEvent('indexedDBChange', { detail: eventDetail });
        window.dispatchEvent(event);  // Dispatch event globally
    }
    broadcastChange(key) {
        const channel = new BroadcastChannel('indexedDB_channel');
        const message = key !== null ? { key } : { key: null, message: 'store cleared' };
        channel.postMessage(message);
    }
}



const storage = new StorageDB();

const customStorage = {
    async setItem(key, value) {
        return await storage.setItem(key, value);
    },
    async getItem(key) {
        return await storage.getItem(key);
    },
    async removeItem(key) {
        return await storage.removeItem(key);
    },
    async clear() {
        return await storage.clear();
    },
};

const channel = new BroadcastChannel('indexedDB_channel');
channel.addEventListener('message', (event) => {
    if (event.data && event.data.key) {
        const customEvent = new CustomEvent('indexedDBChange', { detail: event.data });
        window.dispatchEvent(customEvent);
    }
});


