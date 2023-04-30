import {createSlice} from '@reduxjs/toolkit';

const UserManagementSlice = createSlice({
   name: 'userManagement',
   initialState: {
      role: null, // flag used to redirect to home/investor/expert/ pages
      currentUser: null, // used to get current user object with stored details
   },
   reducers: {
      setCurrentUser(state,action){
         state.currentUser = action.payload; // function to set user object
      },

      setRole(state, action){ // function to set role of user
         state.role = action.payload;
      }
   }
})

export const UserManagementSliceActions = UserManagementSlice.actions;
export default UserManagementSlice;