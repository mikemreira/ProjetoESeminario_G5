import { Outlet, Route, RouterProvider, createBrowserRouter, createRoutesFromElements } from 'react-router-dom'
import SignUp from './user/SignUp.tsx'
import Home from './Home'
import LogIn from './user/LogIn.tsx'
import './App.css'
import Success from './user/Success.jsx'
import NavBar from './NavBar.tsx'
import Obras from "./obra/Obras.tsx"
import LogOut from "./user/LogOut.tsx"
import {AuthnContainer} from "./context/Authn.tsx";
import Profile from "./user/Profile.tsx";
import AddObra from "./obra/AddObra.tsx";
import ObrasInfo from "./obra/ObrasInfo.tsx";
import Registos from "./registos/Registos.tsx";
import ObraRegistos from "./obra/ObraRegistos.tsx";
import ObraFuncionarios from "./obra/ObraFuncionarios.tsx";
import ObraFuncionarioInfo from "./obra/ObraFuncionarioInfo.tsx";
import ObraRegistosOfUser from "./obra/ObraRegistosOfUser.tsx";
import ObraRegistosOfAllUsers from "./obra/ObraRegistosOfAllUsers.tsx";
import InviteToObra from "./convite/InviteToObra.tsx";
import ObraRegistosOfAllPendingUsers from "./obra/ObraRegistosOfAllPendingUsers.tsx";
import ChangePassword from "./user/ChangePassword.tsx";

const AppLayout = () => {
  return (
  <>
    <AuthnContainer>
      <NavBar/>
      <Outlet/>
    </AuthnContainer>
  </>
  )
}

const router = createBrowserRouter([
  {
    "path": "/",
    "element": <AppLayout/>,
    "children": [
      {
          "path": "/",
          "element": <Home/>,
      },
      {
          "path": "/login",
          "element": <LogIn />
      },
      {
        "path": "/logout",
        "element": <LogOut />
      },
      {
        "path": "/signup",
        "element": <SignUp/>
      },
      {
        "path": "/success",
        "element": <Success/>
      },
      {
        "path": "/obras",
        "element": <Obras/>
      },
      {
        "path": "/obras/:oid",
        "element": <ObrasInfo/>
      },
      {
        "path": "/obras/:oid/registers",
        "element": <ObraRegistos/>
      },
      {
        "path": "/obras/:oid/registers/:uid",
        "element": <ObraRegistosOfUser/>
      },
      {
        "path": "/obras/:oid/registers/all",
        "element": <ObraRegistosOfAllUsers/>
      },
      {
        "path": "/obras/:oid/registers/pending",
        "element": <ObraRegistosOfAllPendingUsers/>
      },
      {
        "path": "/obras/:oid/funcionarios",
        "element": <ObraFuncionarios/>
      },
      {
        "path": "/obras/:oid/funcionarios/:uid",
        "element": <ObraFuncionarioInfo/>
      },
      {
        "path": "/obras/:oid/funcionario/invite",
        "element": <InviteToObra/>
      },
      {
        "path": "/profile",
        "element": <Profile />
      },
      {
        "path": "/profile/changePassword",
        "element": <ChangePassword />
      },
      {
        "path": "/addObra",
        "element": <AddObra/>
      },
      {
        "path": "/registos",
        "element": <Registos/>
      }
  ]}
])

function App() {

  return (
    
      <RouterProvider router={router}>
      
      </RouterProvider>

  )
}

export default App
