import { Outlet, Route, RouterProvider, createBrowserRouter, createRoutesFromElements } from 'react-router-dom'
import SignUp from './user/SignUp'
import Home from './Home'
import LogIn from './user/LogIn'
import './App.css'
import Success from './user/Success'
import NavBar from './NavBar'
import Obras from "./obra/Obras"
import LogOut from "./user/LogOut"
import {AuthnContainer} from "./context/Authn";
import Profile from "./user/Profile";
import AddObra from "./obra/AddObra";
import ObrasInfo from "./obra/ObrasInfo";
import Registos from "./registos/Registos";
import ObraRegistos from "./obra/ObraRegistos";
import ObraFuncionarios from "./obra/ObraFuncionarios";
import ObraFuncionarioInfo from "./obra/ObraFuncionarioInfo";
import ObraRegistosOfUser from "./obra/ObraRegistosOfUser";
import ObraRegistosOfAllUsers from "./obra/ObraRegistosOfAllUsers";
import InviteToObra from "./convite/InviteToObra";
import ObraRegistosOfAllPendingUsers from "./obra/ObraRegistosOfAllPendingUsers";
import ChangePassword from "./user/ChangePassword";

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
