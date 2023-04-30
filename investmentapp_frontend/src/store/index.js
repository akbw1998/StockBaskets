import {configureStore} from '@reduxjs/toolkit'
import UserManagementSlice from './UserManagement-slice';

// configure the reducer functions of UserManagementSlice in store
const store = configureStore({
   reducer: {
      userManagement: UserManagementSlice.reducer
   }
});

// export store for setting context to 'App' component
export default store;